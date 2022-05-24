package com.github.vfyjxf.jeiutilities.asm;

import com.github.vfyjxf.jeiutilities.JEIUtilities;
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
        String internalName = toInternalClassName(transformedName);
        if ("mezz/jei/gui/recipes/RecipesGui".equals(internalName)) {
            if (JeiUtilitiesConfig.isEnableHistory()) {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(basicClass);
                classReader.accept(classNode, 0);
                for (MethodNode methodNode : classNode.methods) {
                    if ("show".equals(methodNode.name)) {
                        JEIUtilities.logger.info("Transforming : " + internalName + ";" + methodNode.name + methodNode.desc);
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
        if ("mezz/jei/startup/JeiStarter".equals(internalName)) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            for (MethodNode methodNode : classNode.methods) {
                if ("start".equals(methodNode.name)) {
                    JEIUtilities.logger.info("Transforming : " + internalName + ";" + methodNode.name + methodNode.desc);
                    //patch create method
                    {
                        AbstractInsnNode target = methodNode.instructions.getFirst();
                        for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                            if (node.getOpcode() == Opcodes.NEW && node instanceof TypeInsnNode) {
                                TypeInsnNode typeInsnNode = (TypeInsnNode) node;
                                if ("mezz/jei/gui/overlay/bookmarks/BookmarkOverlay".equals(typeInsnNode.desc)) {
                                    methodNode.instructions.remove(typeInsnNode.getNext());
                                    methodNode.instructions.remove(typeInsnNode);
                                }
                                if ("mezz/jei/bookmarks/BookmarkList".equals(typeInsnNode.desc)) {
                                    methodNode.instructions.remove(typeInsnNode.getNext());
                                    methodNode.instructions.remove(typeInsnNode);
                                }
                            }
                            if (node.getOpcode() == Opcodes.INVOKESPECIAL && node instanceof MethodInsnNode) {
                                MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                                if ("mezz/jei/gui/overlay/bookmarks/BookmarkOverlay".equals(methodInsnNode.owner) && "<init>".equals(methodInsnNode.name)) {
                                    methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                    methodInsnNode.owner = "com/github/vfyjxf/jeiutilities/gui/bookmark/AdvancedBookmarkOverlay";
                                    methodInsnNode.name = "create";
                                    methodInsnNode.desc = "(Lmezz/jei/bookmarks/BookmarkList;Lmezz/jei/gui/GuiHelper;Lmezz/jei/gui/GuiScreenHelper;)Lmezz/jei/gui/overlay/bookmarks/BookmarkOverlay;";
                                }
                                if ("mezz/jei/bookmarks/BookmarkList".equals(methodInsnNode.owner) && "<init>".equals(methodInsnNode.name)) {
                                    methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                    methodInsnNode.owner = "com/github/vfyjxf/jeiutilities/jei/bookmark/RecipeBookmarkList";
                                    methodInsnNode.name = "create";
                                    methodInsnNode.desc = "(Lmezz/jei/ingredients/IngredientRegistry;)Lmezz/jei/bookmarks/BookmarkList;";
                                }
                            }
                        }
                    }
                    //patch set JeiUtilitiesPlugin.recipeRegistry
                    {
                        AbstractInsnNode target = null;
                        for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node instanceof MethodInsnNode) {
                                MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                                if ("mezz/jei/bookmarks/BookmarkList".equals(methodInsnNode.owner) && "loadBookmarks".equals(methodInsnNode.name)) {
                                    target = methodInsnNode;
                                    while (!(target instanceof LabelNode)) {
                                        target = target.getPrevious();
                                    }
                                    break;
                                }
                            }
                        }
                        if (target != null) {
                            InsnList insnList = new InsnList();
                            //load recipeRegistry
                            insnList.add(new VarInsnNode(Opcodes.ALOAD, 14));
                            //set field
                            insnList.add(new MethodInsnNode(Opcodes.PUTSTATIC,
                                    "com/github/vfyjxf/jeiutilities/jei/JeiUtilitiesPlugin",
                                    "recipeRegistry",
                                    "Lmezz/jei/api/IRecipeRegistry;", false));
                            methodNode.instructions.insertBefore(target, insnList);
                        }
                    }
                }
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        return basicClass;
    }

    private String toInternalClassName(String className) {
        return className.replace('.', '/');
    }
}
