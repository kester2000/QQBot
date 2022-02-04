package org.Kester.QQBot;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;
import org.Kester.QQBot.Games.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.Kester.QQBot.CONSTANTS.*;

public class MessageHandler {

    @SuppressWarnings("serial")
    private static List<String> gameNameList = new ArrayList<String>() {
        {
            for (DeclaredElement element : DeclaredElement.values()) {
                add(element.CN);
            }
        }
    };

    private static List<Game> gameList = new ArrayList<Game>();

    public static List<Game> getGameList() {
        return gameList;
    }

    public static void showRule(DeclaredElement instance, MessageEvent messageEvent) throws IOException {
        String NAME = instance.CN;
        String RULE = instance.RULE;
        String[] COMMANDS = instance.COMMANDS;
        if (!new File("RULES").exists()) {
            new File("RULES").mkdirs();
        }
        File file = new File("RULES/" + NAME.substring(1) + "Rule.png");
        if (!file.exists()) {
            int width = 620;
            int height = 800;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(backgroundColor);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(textColor);
            graphics.setFont(titleFont);
            graphics.drawString(instance.CN, 250, 40);
            graphics.setFont(infoFont);
            String str = "    " + RULE + "\n\n" //
                    + "命令1：" + NAME + " rule、规则 -> 返回该篇帮助\n" //
                    + "命令2：" + NAME + " init、创建 -> 创建游戏，等待加入\n" //
                    + "命令3：" + NAME + " in、加入 昵称 -> 以某昵称加入游戏\n" //
                    + "\t\t\t\t例：'" + NAME + " in 宽容' '" + NAME + " 加入 宽容'\n" //
                    + "命令4：" + NAME + " out、退出 -> 退出已经加入的游戏\n" //
                    + "命令5：" + NAME + " start、开始 -> 开始游戏\n" //
                    + "命令6：" + NAME + " stop、停止 -> 停止当前局\n" //
                    + "命令7：" + NAME + " yes、确认 -> 确认停止当前局\n" //
                    //+ "命令8：" + NAME + " gg、认输 -> 当前局认输\n\n"
                    ;
            for (int i = 0; i < COMMANDS.length; i++) {
                str += "场内命令" + (i + 1) + "：" + NAME + " " + COMMANDS[i] + "\n";
            }
            str += "\n\n" //
                    + "机器人作者：宽容 QQ：877367792\n" //
                    + "QQ群：jzj群 号码：1059834024\n";
            int x = 10, y = 100;
            for (int i = 0; i < str.length(); i++) {
                graphics.drawString(String.valueOf(str.charAt(i)), x, y);
                if ((str.charAt(i) + "").getBytes().length == 1) {
                    x += infoFont.getSize() / 2;
                } else {
                    x += infoFont.getSize();
                }
                if (width - x <= 40 || String.valueOf(str.charAt(i)).equals("\n")) {
                    y += infoFont.getSize() + 5;
                    x = infoFont.getSize();
                }
            }
            try {
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ExternalResource externalResource = ExternalResource.create(file);
        Message message = messageEvent.getSubject().uploadImage(externalResource);
        messageEvent.getSubject().sendMessage(message);
        externalResource.close();
    }

    public static void HandleMessageEvent(MessageEvent messageEvent) throws Exception {
        String msg = messageEvent.getMessage().contentToString().trim();
        long senderId = messageEvent.getSender().getId();
        boolean isGroup = (messageEvent.getClass() == GroupMessageEvent.class);
        if (msg.startsWith("-test") || msg.startsWith("-测试")) {
            if (!isGroup) throw new Exception("请在群里测试");
            msg = msg.replaceFirst("-test", "").replaceFirst("-测试", "").trim();
            if (msg.startsWith("g") || msg.startsWith("群")) {
                msg = msg.replaceFirst("g", "").replaceFirst("群", "").trim();
                isGroup = true;
            } else if (msg.startsWith("f") || msg.startsWith("私")) {
                msg = msg.replaceFirst("f", "").replaceFirst("私", "").trim();
                isGroup = false;
            } else {
                throw new Exception("测试缺少参数");
            }

            String s = msg.split("\\s")[0];
            senderId = Long.parseLong(s);

            msg = msg.replaceFirst(s, "").trim();
        }
        if (msg.startsWith("-list") || msg.startsWith("-游戏列表")) {
            String str = "游戏列表";
            for (String s1 : gameNameList)
                str += "\n" + s1;
            messageEvent.getSubject().sendMessage(str);
        } else {
            DeclaredElement instance = DeclaredElement.findEnumByAllName(msg);
            if (instance == null) return;
            if (msg.startsWith(instance.CN))
                msg = msg.replaceFirst(instance.CN, "").trim();
            else if (msg.toLowerCase().startsWith(instance.EN))
                msg = msg.toLowerCase().replaceFirst(instance.EN, "").trim();
            else throw new Exception("代码bug，游戏类型出错");
            GroupMessageEvent groupMessageEvent;
            try {
                groupMessageEvent = (GroupMessageEvent) messageEvent;
            } catch (Exception e) {
                groupMessageEvent = null;
            }
            if (msg.startsWith("rule") || msg.startsWith("规则")) {
                MessageHandler.showRule(instance, messageEvent);
            } else if (msg.startsWith("init") || msg.startsWith("创建")) {
                // 创建一个新的游戏
                if (!isGroup) throw new Exception("不能在私聊中创建游戏");
                for (Game g : gameList) {
                    if (msg.startsWith(g.getName()) && g.getGroupId() == groupMessageEvent.getGroup().getId())
                        throw new Exception("已经有存在的一局" + g.getName());
                }
                Game game;
                try {
                    game = instance.CLAZZ.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    game = null;
                }
                if (game == null) throw new Exception(instance.CN + "创建失败");
                game.setGroup(groupMessageEvent.getGroup());
                gameList.add(game);
                messageEvent.getSubject().sendMessage(game.getName() + "创建成功");
            } else {
                for (Game game : gameList) {
                    if (instance.CLAZZ == game.getClass() &&
                            ((isGroup && groupMessageEvent.getGroup().getId() == game.getGroupId()) || game.getIndexById(senderId) != -1)) {
                        if ((msg.startsWith("yes") || msg.startsWith("确认"))) {
                            if (!isGroup) throw new Exception("请在群聊中进行");
                            if (!game.isKillingGame()) throw new Exception("请先发送停止命令");
                            game.kill(); // kill方法：用于停止游戏内线程释放资源
                            messageEvent.getSubject().sendMessage(game.getName() + "已停止");
                        } else if (msg.startsWith("stop") || msg.startsWith("停止")) {
                            if (!isGroup) throw new Exception("请在群聊中进行");
                            game.setKillingGame(true);
                            messageEvent.getSubject().sendMessage("确认要停止吗？(" + game.getName() + " 确认)");
                        } else if (msg.startsWith("in") || msg.startsWith("参加")) {
                            if (!isGroup) throw new Exception("请在群聊中进行");
                            String nickName = msg.replaceFirst("in", "").replace("参加", "").trim();
                            game.addPlayer(senderId, nickName);
                            At at = new At(groupMessageEvent.getSender().getId());
                            MessageChain messages = at.plus("加入成功").plus("\n目前成员：");
                            for (Game.Player p : game.getPlayers()) {
                                messages = messages.plus(p.getNickName() + " ");
                            }
                            messageEvent.getSubject().sendMessage(messages);
                        } else if (msg.startsWith("out") || msg.startsWith("退出")) {
                            if (!isGroup) throw new Exception("请在群聊中进行");
                            game.deletePlayer(senderId);
                            At at = new At(groupMessageEvent.getSender().getId());
                            MessageChain messages = at.plus("退出成功").plus("\n目前成员：");
                            for (Game.Player p : game.getPlayers()) {
                                messages = messages.plus(p.getNickName() + " ");
                            }
                            messageEvent.getSubject().sendMessage(messages);
                        } else if (msg.startsWith("start") || msg.startsWith("开始")) {
                            if (!isGroup) throw new Exception("请在群聊中进行");
                            game.start();
                        } else {
                            game.doByEvent(messageEvent, msg, senderId, isGroup);
                        }
                        return;
                    }
                }
                messageEvent.getSubject().sendMessage("本群还没有已创建的" + instance.CN);
            }
        }
    }
}