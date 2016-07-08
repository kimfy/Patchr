package net.kimfy.patchr.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class Transformer implements IClassTransformer
{
    public static final Logger LOG = LogManager.getLogger("Patchr");
    public static final String CLASS_MOD_BLOCKS = "vswe.stevescarts.Blocks.ModBlocks";

    @Override
    public byte[] transform(String className, String transformedName, byte[] bytes)
    {
        if (CLASS_MOD_BLOCKS.equals(className))
            return transform(bytes);
        return bytes;
    }

    private static byte[] transform(byte[] bytes)
    {
        LOG.info("Patching ", CLASS_MOD_BLOCKS);
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);

            final String METH      = "<clinit>";
            final String METH_DESC = "()V";

            for (MethodNode method : classNode.methods)
            {
                if (METH.equals(method.name) && METH_DESC.equals(method.desc))
                {
                    for (int i = 0; i < method.instructions.size(); i++)
                    {
                        AbstractInsnNode node = method.instructions.get(i);
                        if (node.getOpcode() == Opcodes.LDC && "assembler".equals((((LdcInsnNode) node).cst)))
                        {
                            ((LdcInsnNode) node).cst = "sc2assembler";
                            break;
                        }
                    }
                }
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            LOG.info("Done patching ", CLASS_MOD_BLOCKS);
            return classWriter.toByteArray();
        }
        catch (Exception ex)
        {
            LOG.error("Something went wrong when patching ", CLASS_MOD_BLOCKS, ex);
        }
        return bytes;
    }
}