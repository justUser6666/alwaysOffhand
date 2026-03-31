    package justUser.alwaysoffhand;

    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
    import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
    import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
    import net.minecraft.client.MinecraftClient;
    import net.minecraft.client.gl.RenderPipelines;
    import net.minecraft.item.ItemStack;
    import net.minecraft.registry.Registries;
    import net.minecraft.util.Identifier;
    import net.minecraft.client.gui.screen.ingame.*;
    import org.lwjgl.glfw.GLFW;

    import java.util.Arrays;

    public class main implements ClientModInitializer {
        Class[] allowedScreens = new Class[] {
                CraftingScreen.class,
                AnvilScreen.class,
                FurnaceScreen.class,
                LoomScreen.class,
                GenericContainerScreen.class,
                HorseScreen.class,
                Generic3x3ContainerScreen.class,
                CrafterScreen.class,
                HopperScreen.class,
                EnchantmentScreen.class,
                StonecutterScreen.class,
                GrindstoneScreen.class,
                CartographyTableScreen.class,
                SmithingScreen.class,
                BlastFurnaceScreen.class,
                SmokerScreen.class,
                BeaconScreen.class,
                ShulkerBoxScreen.class,
                MerchantScreen.class
        };


        private boolean wasLeftMouseDown = false;
        protected static boolean isChanged = false;

        public void onInitializeClient() {

            HudElementRegistry.addLast(Identifier.of("visiblearmorslot", "hud"), (context, tickCounter) -> {
                MinecraftClient client = MinecraftClient.getInstance();

                if (Arrays.stream(allowedScreens).noneMatch(cls -> cls.isInstance(client.currentScreen))) return;

                int centerX = client.getWindow().getScaledWidth() / 2;
                int centerY = client.getWindow().getScaledHeight() / 2;

                int x = centerX - 88 - 20;
                int y = centerY - 30;

                context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    Identifier.of("minecraft", "textures/gui/sprites/container/slot.png"),
                    x, y,
                    0, 0,
                    18, 18,
                    18, 18);

                boolean isLeftMouseDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                boolean isRightMouseDown = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
                double mouseX = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();

                if (mouseX >= x && mouseX < x + 18 &&
                    mouseY >= y && mouseY < y + 18) {

                    context.fill(x+1, y+1, x + 17, y + 17, 0xffC0C0C0);

                    ItemStack stack = client.player.getInventory().getStack(40);
                    if (!stack.isEmpty()) context.drawItemTooltip(client.textRenderer, stack, (int)mouseX, (int)mouseY);

                    if (!wasLeftMouseDown && isLeftMouseDown) left.defaultClick();

                    wasLeftMouseDown = isLeftMouseDown;
                }


                ItemStack stack = client.player.getInventory().getStack(40);
                if (!stack.isEmpty()) {
                    int count = stack.getCount();
                    if (Registries.ITEM.getId(client.player.currentScreenHandler.getCursorStack().getItem()) == Registries.ITEM.getId(stack.getItem()) && isChanged) {
                        System.out.println("씨발 왜 안돼");
                        client.player.getOffHandStack().setCount(count + client.player.currentScreenHandler.getCursorStack().getCount());
                        if ((count + client.player.currentScreenHandler.getCursorStack().getCount()) > stack.getMaxCount()) {
                            client.player.getOffHandStack().setCount(stack.getMaxCount());
                            client.player.currentScreenHandler.getCursorStack().setCount((count + client.player.currentScreenHandler.getCursorStack().getCount()) - stack.getMaxCount());
                        }
                    }
                    isChanged = false;
                    context.drawItem(
                        client.player.getOffHandStack(),
                        x+1,y+1);
                    if (count > 1) context.drawText(client.textRenderer, String.valueOf(count), x + 18 - client.textRenderer.getWidth(String.valueOf(count)), y + 10, 0xFFFFFFFF, true);
                }
            });

            /////////////////////////////////
            // drop item block
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                ScreenMouseEvents.allowMouseRelease(screen).register((screen1, context) -> {
                    int centerX = client.getWindow().getScaledWidth() / 2;
                    int centerY = client.getWindow().getScaledHeight() / 2;

                    int x = centerX - 88 - 20;
                    int y = centerY - 30;

                    if (context.x() >= x && context.x() < x + 18 && context.y() >= y && context.y() < y + 18 && !client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                        return false;
                    }
                    return true;
                });
            });
            /////////////////////////////////
        }
    }