package org.Kester.QQBot.Games;

import net.mamoe.mirai.event.events.MessageEvent;

import java.io.IOException;
import java.util.Objects;

public class TXWZGame extends Game {

    protected int[] health = new int[9999];
    protected String[] role = new String[9999];
    protected String[] target = new String[9999];

    public TXWZGame() {
        super();
        name = "天下无贼";
        minPlayer = 3;
        maxPlayer = 12;

        gridMarginTop = 40;
        gridMarginBottom = 40;
        gridMarginLeft = 40;
        gridMarginRight = 40;

        width = 60;
        height = 30;
        firstColWidth = 60;
        myHighLight = param1 -> param1 % 3 == 1;
        myFirstColString = param1 -> {
            if (param1 == 0) return "昵称";
            if (param1 % 3 == 1) return "血量";
            if (param1 % 3 == 2) return "身份";
            return "目标";
        };
    }

    @Override
    public void start() throws Exception {
        super.start();

        for (int i = 0; i < players.size(); i++) {
            info[0][i] = players.get(i).getNickName();
            info[1][i] = String.valueOf(10);
            health[i] = 10;
            role[i] = "";
            target[i] = "";
        }

        showInfo(players.size(), 2);

        loop();
    }

    @Override
    protected void autoDo(int i) {
        vis[i] = true;
        health[i] = -9999;
        role[i] = "";
        target[i] = "";
    }

    @Override
    protected void cal() throws IOException {
        boolean hasPolice = false;
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(role[i], "警")) {
                hasPolice = true;
                break;
            }
        }
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(role[i], "民")) {
                if (!hasPolice) health[i]--;
            } else if (Objects.equals(role[i], "警")) {
                int id = getIndexByNickName(target[i]);
                if (Objects.equals(role[id], "贼")) {
                    health[i]++;
                    health[id] -= 9999;
                } else health[i] -= 2;
            } else if (Objects.equals(role[i], "贼")) {
                int id = getIndexByNickName(target[i]);
                if (!Objects.equals(role[id], "警")) {
                    health[i]++;
                    health[id] -= 2;
                } else health[i] -= 9999;
            }
        }

        int cnt = 0;
        for (int i = 0; i < players.size(); i++) {
            if (health[i] < 0) health[i] = 0;
            if (health[i] > 0) cnt++;
        }


        for (int i = 0; i < players.size(); i++) {
            info[3 * round - 1][i] = role[i];
            info[3 * round][i] = target[i];
            info[3 * round + 1][i] = String.valueOf(health[i]);
        }

        showInfo(players.size(), round * 3 + 2);
        if (cnt <= 2) {
            group.sendMessage("游戏结束");
            kill();
        }

        for (int i = 0; i < players.size(); i++) {
            if (health[i] > 0) vis[i] = false;
        }
    }

    @Override
    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        super.doByEvent(messageEvent, msg, senderId, isGroup);
        int id = getIndexById(senderId);

        msg = msg.replace(getName(), "").trim();

        if (health[id] <= 0) throw new Exception("你已经死亡");
        if (vis[id]) throw new Exception("你已经进行过本回合操作");

        if (msg.startsWith("民")) {
            role[id] = "民";
            target[id] = "";
            vis[id] = true;
            messageEvent.getSubject().sendMessage("成为民，成功");
        } else if (msg.startsWith("警")) {
            if (Objects.equals(role[id], "贼")) throw new Exception("你不能从贼变成警");
            msg = msg.replaceFirst("警", "").trim();
            int id2 = getIndexByNickName(msg);
            if (id2 == -1) throw new Exception("找不到你的目标对象");
            if (id2 == id) throw new Exception("目标不能是自己");
            if (health[id2] <= 0) throw new Exception("目标对象已死亡");
            role[id] = "警";
            target[id] = msg;
            vis[id] = true;
            messageEvent.getSubject().sendMessage("成为警，抓" + msg + "，成功");
        } else if (msg.startsWith("贼")) {
            if (Objects.equals(role[id], "警")) throw new Exception("你不能从警变成贼");
            msg = msg.replaceFirst("贼", "").trim();
            int id2 = getIndexByNickName(msg);
            if (id2 == -1) throw new Exception("找不到你的目标对象");
            if (id2 == id) throw new Exception("目标不能是自己");
            if (health[id2] <= 0) throw new Exception("目标对象已死亡");
            role[id] = "贼";
            target[id] = msg;
            vis[id] = true;
            messageEvent.getSubject().sendMessage("成为贼，偷" + msg + "，成功");
        } else {
            messageEvent.getSubject().sendMessage("未知命令");
        }
    }

}
