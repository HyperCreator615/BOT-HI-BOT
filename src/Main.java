import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static final String HOST = "Mega_Martin.aternos.me";
    private static final int PORT = 25565;
    private static final String USERNAME = "Martin-AFK-BOT";
    private static final String PASSWORD = "chatgpt.chadgpt";

    public static void main(String[] args) {
        connect();
    }

    private static void connect() {
        MinecraftProtocol protocol = new MinecraftProtocol(USERNAME);
        Session session = new TcpClientSession(new InetSocketAddress(HOST, PORT), protocol);

        session.addListener(event -> {
            if (event.getPacket() instanceof ClientboundLoginPacket) {
                System.out.println("✅ Joined server");

                // Auto register/login
                session.send(new com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket(
                        "/register " + PASSWORD
                ));
                session.send(new com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket(
                        "/login " + PASSWORD
                ));

                startAntiAFK(session);
            }
        });

        session.connect();

        session.addDisconnectListener((s, reason, error) -> {
            System.out.println("❌ Disconnected: " + reason + " Reconnecting in 5s...");
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            connect();
        });
    }

    private static void startAntiAFK(Session session) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                session.send(new com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket(" "));
            }
        }, 60000, 60000);
    }
}
