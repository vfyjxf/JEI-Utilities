package com.github.vfyjxf.jeiutilities.asm;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@SuppressWarnings("unused")
public class JeiUtilitiesClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("mezz/jei/gui/recipes/RecipesGui".equals(toInternalClassName(transformedName))) {
            if (JeiUtilitiesConfig.enableHistory) {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);
                for (MethodNode methodNode : classNode.methods) {
                    if ("show".equals(methodNode.name)) {
                        AbstractInsnNode target = methodNode.instructions.getFirst();
                        while (target.getOpcode() != Opcodes.RETURN) {
                            target = target.getNext();
                        }
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        insnList.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "com/github/vfyjxf/jeiutilities/jei/JeiHooks",
                                "onSetFocus",
                                "(Lmezz/jei/api/recipe/IFocus;)V",
                                false)
                        );

                        methodNode.instructions.insertBefore(target, insnList);
                        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                        classNode.accept(classWriter);
                        return classWriter.toByteArray();
                    }
                }

            }
        }
        return basicClass;
    }

    private String toInternalClassName(String className) {
        return className.replace('.', '/');
    }
}
