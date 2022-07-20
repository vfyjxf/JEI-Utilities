var Opcodes = Java.type('org.objectweb.asm.Opcodes');

function transConstruct(method, src, dst) {
    var i = method.instructions.iterator();
    while (i.hasNext()) {
        var n = i.next();
        if (n.getOpcode() === Opcodes.NEW) {
            if (n.desc === src) {
                n.desc = dst;
            }
        } else if (n.getOpcode() === Opcodes.INVOKESPECIAL) {
            if (n.owner === src) n.owner = dst;
        }
    }
}

function transConstructToFactory(method, oldOwner, newOwner, name, desc) {
    var instructions = method.instructions.toArray();
    for (var i = 0; i < instructions.length; i++) {
        var n = instructions[i];
        if (n.getOpcode() === Opcodes.NEW) {
            if (n.desc === oldOwner) {
                method.instructions.remove(n.getNext()); //remove insn dup
                method.instructions.remove(n); //remove type insn
            }
        } else if (n.getOpcode() === Opcodes.INVOKESPECIAL) {
            if (n.owner === oldOwner) {
                n.setOpcode(Opcodes.INVOKESTATIC);
                n.owner = newOwner;
                n.name = name;
                n.desc = desc;
            }
        }
    }
}

function transConstructWithOrdinal(method, oldOwner, newOwner, name, desc, ordinal) {
    var instructions = method.instructions.toArray();
    var count = -1;
    for (var i = 0; i < instructions.length; i++) {
        var n = instructions[i];
        if (n.getOpcode() === Opcodes.NEW) {
            if (n.desc === oldOwner) {
                count++;
                if (count === ordinal) {
                    method.instructions.remove(n.getNext()); //remove insn dup
                    method.instructions.remove(n); //remove type insn
                }
            }
        } else if (n.getOpcode() === Opcodes.INVOKESPECIAL) {
            if (n.owner === oldOwner) {
                if (count === ordinal) {
                    n.setOpcode(Opcodes.INVOKESTATIC);
                    n.owner = newOwner;
                    n.name = name;
                    n.desc = desc;
                }
            }
        }
    }
}

function initializeCoreMod() {
    return {
        'transBookmarkInputHandler': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.startup.JeiStarter',
                'methodName': 'start',
                'methodDesc': '(Lmezz/jei/forge/events/RuntimeEventSubscriptions;)V'
            }, 'transformer': function (method) {
                transConstructToFactory(method,
                    'mezz/jei/input/mouse/handlers/BookmarkInputHandler',
                    'com/github/vfyjxf/jeiutilities/gui/input/RecipeBookmarkInputHandler',
                    'create',
                    '(Lmezz/jei/input/CombinedRecipeFocusSource;Lmezz/jei/bookmarks/BookmarkList;)Lmezz/jei/input/mouse/IUserInputHandler;'
                )
                return method;
            }
        },
        'transFocusInputHandler': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.startup.JeiStarter',
                'methodName': 'start',
                'methodDesc': '(Lmezz/jei/forge/events/RuntimeEventSubscriptions;)V'
            }, 'transformer': function (method) {
                transConstructToFactory(method,
                    'mezz/jei/input/mouse/handlers/FocusInputHandler',
                    'com/github/vfyjxf/jeiutilities/gui/input/ExtendedFocusInputHandler',
                    'create',
                    '(Lmezz/jei/input/CombinedRecipeFocusSource;Lmezz/jei/gui/recipes/RecipesGui;)Lmezz/jei/input/mouse/IUserInputHandler;'
                )
                return method;
            }
        },
        'transBookmarkConfig': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.forge.startup.ClientLifecycleHandler',
                'methodName': '<init>',
                'methodDesc': '(Lmezz/jei/forge/network/NetworkHandler;Lmezz/jei/gui/textures/Textures;Lmezz/jei/forge/config/JEIClientConfigs;Lmezz/jei/core/config/IServerConfig;)V'
            }, 'transformer': function (method) {
                transConstructToFactory(method,
                    'mezz/jei/config/BookmarkConfig',
                    'com/github/vfyjxf/jeiutilities/jei/bookmark/RecipeBookmarkConfig',
                    'create',
                    '(Ljava/io/File;)Lmezz/jei/config/BookmarkConfig;'
                )
                return method;
            }
        },
        'transBookmarkOverlay': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.startup.JeiStarter',
                'methodName': 'start',
                'methodDesc': '(Lmezz/jei/forge/events/RuntimeEventSubscriptions;)V'
            }, 'transformer': function (method) {
                transConstructToFactory(method,
                    'mezz/jei/gui/overlay/bookmarks/BookmarkOverlay',
                    'com/github/vfyjxf/jeiutilities/gui/bookmark/AdvancedBookmarkOverlay',
                    'create',
                    '(Lmezz/jei/bookmarks/BookmarkList;Lmezz/jei/gui/textures/Textures;Lmezz/jei/gui/overlay/IngredientGridWithNavigation;Lmezz/jei/core/config/IClientConfig;Lmezz/jei/core/config/IWorldConfig;Lmezz/jei/gui/GuiScreenHelper;Lmezz/jei/common/network/IConnectionToServer;)Lmezz/jei/gui/overlay/bookmarks/BookmarkOverlay;'
                )
                return method;
            }
        },
        'transIngredientListGrid': {
            'target': {
                'type': 'METHOD',
                'class': 'mezz.jei.startup.JeiStarter',
                'methodName': 'start',
                'methodDesc': '(Lmezz/jei/forge/events/RuntimeEventSubscriptions;)V'
            }, 'transformer': function (method) {
                transConstructWithOrdinal(method,
                    'mezz/jei/gui/overlay/IngredientGrid',
                    'com/github/vfyjxf/jeiutilities/gui/history/AdvancedIngredientListGrid',
                    'create',
                    '(Lmezz/jei/ingredients/RegisteredIngredients;Lmezz/jei/config/IIngredientGridConfig;Lmezz/jei/config/IEditModeConfig;Lmezz/jei/config/IIngredientFilterConfig;Lmezz/jei/core/config/IClientConfig;Lmezz/jei/core/config/IWorldConfig;Lmezz/jei/gui/GuiScreenHelper;Lmezz/jei/api/helpers/IModIdHelper;Lmezz/jei/common/network/IConnectionToServer;)Lmezz/jei/gui/overlay/IngredientGrid;',
                    0)
                return method;
            }
        }
    }
}
