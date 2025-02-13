package me.nonamegmm.mcscore;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
    private static boolean isKKeyPressed = false;
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == KeyEvent.VK_B && !isKKeyPressed) {
            Player player = Minecraft.getInstance().player;
            sendChatMessage(player,"打开购买菜单");
            sendPluginMessage(player);
            isKKeyPressed = true;
        }
        else {
            isKKeyPressed = false;
        }
    }

    private static void sendPluginMessage(Player player) {
        String name = player.getName().getContents();
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) {
            String channel = "mcscore:menu";
            ByteBuf buf = Unpooled.buffer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(baos);
            try {
                dataOut.writeUTF("buy " + name);
            } catch (IOException e) {
                e.printStackTrace();
            }

            buf.writeBytes(baos.toByteArray());
            FriendlyByteBuf friendlyBuf = new FriendlyByteBuf(buf);
            connection.send(new ServerboundCustomPayloadPacket(new ResourceLocation(channel), friendlyBuf));
        }
    }

    private static void sendChatMessage(Player player, String message) {
        TextComponent textComponent = new TextComponent("[MCSCore] " + message);
        player.sendMessage(textComponent, player.getUUID());
    }
}