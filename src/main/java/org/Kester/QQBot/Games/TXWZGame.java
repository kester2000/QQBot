package org.Kester.QQBot.Games;

import net.mamoe.mirai.event.events.MessageEvent;

import java.io.IOException;
import java.util.Objects;

public class TXWZGame extends Game {

    private final class TXWZPlayer extends Player {
        private int health;
        private String role;
        private String target;

        public TXWZPlayer(long id, String nickName) throws Exception {
            super(id, nickName);
            health = 10;
            role = "";
            target = "";
        }

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public void addHealth(int add) {
            this.health += add;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

//    protected int[] health = new int[9999];
//    protected String[] role = new String[9999];
//    protected String[] target = new String[9999];

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
    public Player createPlayer(long id, String nickName) throws Exception {
        return new TXWZPlayer(id, nickName);
    }

    @Override
    public void start() throws Exception {
        super.start();

        for (int i = 0; i < players.size(); i++) {
            info[0][i] = players.get(i).getNickName();
            info[1][i] = String.valueOf(10);
        }

        showInfo(players.size(), 2);

        loop();
    }

    @Override
    protected void autoDo(int i) {
        TXWZPlayer txwzPlayer = (TXWZPlayer) players.get(i);
        txwzPlayer.setVis(true);
        txwzPlayer.setHealth(-9999);
        txwzPlayer.setRole("");
        txwzPlayer.setTarget("");
    }

    @Override
    protected void cal() throws IOException {
        boolean hasPolice = false;
        for (Player player : players) {
            TXWZPlayer txwzPlayer = (TXWZPlayer) player;
            if (Objects.equals(txwzPlayer.getRole(), "警")) {
                hasPolice = true;
                break;
            }
        }
        for (Player player : players) {
            TXWZPlayer txwzPlayer = (TXWZPlayer) player;
            if (Objects.equals(txwzPlayer.getRole(), "民")) {
                if (!hasPolice) txwzPlayer.addHealth(-1);
            } else if (Objects.equals(txwzPlayer.getRole(), "警")) {
                int id = getIndexByNickName(txwzPlayer.getTarget());
                TXWZPlayer targetTXWZPlayer = (TXWZPlayer) players.get(id);
                if (Objects.equals(targetTXWZPlayer.getRole(), "贼")) {
                    txwzPlayer.addHealth(1);
                    targetTXWZPlayer.addHealth(-9999);
                } else txwzPlayer.addHealth(-2);
            } else if (Objects.equals(txwzPlayer.getRole(), "贼")) {
                int id = getIndexByNickName(txwzPlayer.getTarget());
                TXWZPlayer targetTXWZPlayer = (TXWZPlayer) players.get(id);
                if (!Objects.equals(targetTXWZPlayer.getRole(), "警")) {
                    txwzPlayer.addHealth(1);
                    targetTXWZPlayer.addHealth(-2);
                } else txwzPlayer.addHealth(-9999);
            }
        }

        int cnt = 0;
        for (Player player : players) {
            TXWZPlayer txwzPlayer = (TXWZPlayer) player;
            if (txwzPlayer.getHealth() < 0) txwzPlayer.setHealth(0);
            if (txwzPlayer.getHealth() > 0) cnt++;
        }

        for (int i = 0; i < players.size(); i++) {
            TXWZPlayer txwzPlayer = (TXWZPlayer) players.get(i);
            info[3 * round - 1][i] = txwzPlayer.getRole();
            info[3 * round][i] = txwzPlayer.getTarget();
            info[3 * round + 1][i] = String.valueOf(txwzPlayer.getHealth());
        }

        showInfo(players.size(), round * 3 + 2);
        if (cnt <= 2) {
            group.sendMessage("游戏结束");
            kill();
        }

        for (Player player : players) {
            TXWZPlayer txwzPlayer = (TXWZPlayer) player;
            if (txwzPlayer.getHealth() > 0) txwzPlayer.setVis(false);
        }
    }

    @Override
    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        super.doByEvent(messageEvent, msg, senderId, isGroup);

        if (isGroup) throw new Exception("请私聊");
        int id = getIndexById(senderId);

        msg = msg.replace(getName(), "").trim();

        TXWZPlayer txwzPlayer = (TXWZPlayer) players.get(id);
        if (txwzPlayer.getHealth() <= 0) throw new Exception("你已经死亡");
        if (txwzPlayer.isVis()) throw new Exception("你已经进行过本回合操作");

        if (msg.startsWith("民")) {
            txwzPlayer.setRole("民");
            txwzPlayer.setTarget("");
            txwzPlayer.setVis(true);
            messageEvent.getSubject().sendMessage("成为民，成功");
        } else if (msg.startsWith("警")) {
            if (Objects.equals(txwzPlayer.getRole(), "贼")) throw new Exception("你不能从贼变成警");
            msg = msg.replaceFirst("警", "").trim();
            int id2 = getIndexByNickName(msg);
            if (id2 == -1) throw new Exception("找不到你的目标对象");
            if (id2 == id) throw new Exception("目标不能是自己");
            if (((TXWZPlayer) players.get(id2)).getHealth() <= 0) throw new Exception("目标对象已死亡");
            txwzPlayer.setRole("警");
            txwzPlayer.setTarget(msg);
            txwzPlayer.setVis(true);
            messageEvent.getSubject().sendMessage("成为警，抓" + msg + "，成功");
        } else if (msg.startsWith("贼")) {
            if (Objects.equals(txwzPlayer.getRole(), "警")) throw new Exception("你不能从警变成贼");
            msg = msg.replaceFirst("贼", "").trim();
            int id2 = getIndexByNickName(msg);
            if (id2 == -1) throw new Exception("找不到你的目标对象");
            if (id2 == id) throw new Exception("目标不能是自己");
            if (((TXWZPlayer) players.get(id2)).getHealth() <= 0) throw new Exception("目标对象已死亡");
            txwzPlayer.setRole("贼");
            txwzPlayer.setTarget(msg);
            txwzPlayer.setVis(true);
            messageEvent.getSubject().sendMessage("成为贼，偷" + msg + "，成功");
        } else {
            messageEvent.getSubject().sendMessage("未知命令");
        }
    }

}
