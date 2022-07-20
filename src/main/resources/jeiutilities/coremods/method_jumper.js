var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');

function initializeCoreMod() {
    return {
        'moveCreateRecipeManager': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.startup.JeiStarter',
                'methodName': 'start',
                'methodDesc': '(Lmezz/jei/forge/events/RuntimeEventSubscriptions;)V'
            },
            'transformer': function (method) {
                //we should move the creation of the RecipeManager before bookmark loading,because we need to use recipe manager.

                var instructions = method.instructions.toArray();
                for (var i = 0; i < instructions.length; i++) {
                    var n = instructions[i];
                    if (n.getOpcode() === Opcodes.INVOKEVIRTUAL && n.name === 'createBookmarkList') {
                        var bookmarkListLabel = n.getPrevious()
                        while (!(bookmarkListLabel instanceof LabelNode)) {
                            bookmarkListLabel = bookmarkListLabel.getPrevious();
                        }
                        var recipeManagerLabel = bookmarkListLabel.getNext();
                        while (!(recipeManagerLabel instanceof LabelNode)) {
                            recipeManagerLabel = recipeManagerLabel.getNext();
                        }
                        var recipeManagerLabelEnd = recipeManagerLabel.getNext();
                        while (!(recipeManagerLabelEnd instanceof LabelNode)) {
                            recipeManagerLabelEnd = recipeManagerLabelEnd.getNext();
                        }
                        method.instructions.insertBefore(bookmarkListLabel, new JumpInsnNode(Opcodes.GOTO, recipeManagerLabel));
                        var list = new InsnList();
                        list.add(new LabelNode());
                        list.add(new VarInsnNode(Opcodes.ALOAD, 13));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 9))

                        list.add(new VarInsnNode(Opcodes.ALOAD, 8));
                        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                            "mezz/jei/load/PluginLoader",
                            "getIngredientManager",
                            "()Lmezz/jei/api/runtime/IIngredientManager;", false))

                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            'com/github/vfyjxf/jeiutilities/jei/JeiUtilitiesPlugin',
                            'setEarlyValue',
                            '(Lmezz/jei/api/recipe/IRecipeManager;Lmezz/jei/api/helpers/IJeiHelpers;Lmezz/jei/api/runtime/IIngredientManager;)V', false));
                        list.add(new JumpInsnNode(Opcodes.GOTO, bookmarkListLabel));
                        method.instructions.insertBefore(recipeManagerLabelEnd, list);
                        method.instructions.insertBefore(recipeManagerLabel, new JumpInsnNode(Opcodes.GOTO, recipeManagerLabelEnd));
                    }
                }
                return method;
            }
        }
    }
}