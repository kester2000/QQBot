package org.Kester.QQBot.Games;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.Kester.QQBot.CONSTANTS;
import org.Kester.QQBot.MessageHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static org.Kester.QQBot.CONSTANTS.*;

public abstract class Game implements Serializable {


    public static class Player {
        /**
         * 游戏玩家类，存放玩家数据
         * id为玩家QQ号，nickName为2-6位支持中英文(包括全角字符)、数字、下划线和减号，其中中文2位，其他1位
         */
        private long id;
        private String nickName;
        private boolean vis;

        public Player(long id, String nickName) throws Exception {
            this.id = id;
            setNickName(nickName);
            vis = false;
        }

        public long getId() {
            return id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setId(long id) {
            this.id = id;
        }

        /**
         * 验证用户名，支持中英文(包括全角字符)、数字、下划线和减号 (全角及汉字算两位)
         */

        public boolean validateUserName(String userName) {
            String validateStr = "^[\\w\\-－＿[０-９]\u4e00-\u9fa5\uFF21-\uFF3A\uFF41-\uFF5A]+$";
            boolean rs = false;
            rs = matcher(validateStr, userName);
            return rs;
        }

        /**
         * 获取字符串的长度，对双字符(包括汉字)按两位计数
         */

        public static int getStrLength(String value) {
            int valueLength = 0;
            String chinese = "[\u0391-\uFFE5]";
            for (int i = 0; i < value.length(); i++) {
                String temp = value.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
            }
            return valueLength;
        }

        private static boolean matcher(String reg, String string) {
            boolean tem = false;
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(string);
            tem = matcher.matches();
            return tem;
        }

        public void setNickName(String nickName) throws Exception {
            if (!validateUserName(nickName)) throw new Exception("不允许出现特殊字符");
            if (getStrLength(nickName) < 2) throw new Exception("昵称过短");
            if (getStrLength(nickName) > 6) throw new Exception("昵称过长");
            this.nickName = nickName;
        }

        public boolean isVis() {
            return vis;
        }

        public void setVis(boolean vis) {
            this.vis = vis;
        }
    }

    /**
     * qunId为群号
     * state为当前状态
     * players为玩家列表
     * minPlayer 和 maxPlayer为最小最大人数
     */
    protected String name;
    protected int minPlayer;
    protected int maxPlayer;

    protected Group group;


    protected STATE state = STATE.PREPARING;

    protected boolean killingGame = false;

    protected List<Player> players = new ArrayList<>();

    protected String[][] info = new String[9999][9999];

    protected int gridMarginTop;
    protected int gridMarginBottom;
    protected int gridMarginLeft;
    protected int gridMarginRight;

    protected int width;
    protected int height;

    protected int firstColWidth;

    protected int round = 0;
    protected int waitTime = 180;

    //默认不高亮
    protected HighLightInterface myHighLight = param1 -> false;

    protected FirstColString myFirstColString = param1 -> "";

    public Game() {
        new Thread(this::timer).start();
    }


    public Player createPlayer(long id, String nickName) throws Exception {
        return new Player(id, nickName);
    }

    public void addPlayer(long id, String nickName) throws Exception {
        if (state != STATE.PREPARING) throw new Exception("游戏已经开始，添加失败");
        if (players.size() == maxPlayer) throw new Exception("人数已满");
        if (nickName.equals("")) throw new Exception("请输入昵称");
        for (Player p : players) {
            if (p.getId() == id) throw new Exception("你已经加入，添加失败");
            if (Objects.equals(p.getNickName(), nickName)) throw new Exception("昵称重复，添加失败");
        }
        for (Game g : MessageHandler.getGameList()) {
            if (this != g && this.getClass() == g.getClass()) {
                for (Player p : g.players) {
                    if (p.getId() == id) throw new Exception("你已经加入另一个同样的游戏，添加失败");
                }
            }
        }
        players.add(createPlayer(id, nickName));
    }

    public void deletePlayer(long id) throws Exception {
        if (state != STATE.PREPARING) throw new Exception("游戏已经开始，退出失败");
        for (Player p : players) {
            if (p.getId() == id) {
                players.remove(p);
                return;
            }
        }
        throw new Exception("你不在该游戏中");
    }

    public void start() throws Exception {
        if (state != STATE.PREPARING) throw new Exception("游戏已经开始");
        if (players.size() < minPlayer) throw new Exception("游戏人数不足");
        if (players.size() > maxPlayer) throw new Exception("代码bug，游戏人数过多");
        state = STATE.PLAYING;
    }

    public void kill() {
        state = STATE.STOPPING;
        MessageHandler.getGameList().remove(this);
    }

    protected void loop() throws IOException {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (this.state == CONSTANTS.STATE.PLAYING) {
            round++;
            int t = 0;
            while (this.state == CONSTANTS.STATE.PLAYING && t < waitTime) {
                t++;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean flag = true;
                for (Player player : players) {
                    if (!player.isVis()) {
                        flag = false;
                        break;
                    }
                }
                if (flag) break;
                if (waitTime - t == 120 || waitTime - t == 60 || waitTime - t == 30 || waitTime - t == 15) {
                    MessageChainBuilder builder = new MessageChainBuilder();
                    for (Player player : players) {
                        if (!player.isVis()) {
                            At at = new At(player.getId());
                            builder.add(at);
                        }
                    }
                    builder.add("仍未发送消息，" + (waitTime - t) + "秒后自动死亡");
                    MessageChain messages = builder.build();
                    group.sendMessage(messages);
                }
            }
            for (int i = 0; i < players.size(); i++) {
                if (!players.get(i).isVis()) autoDo(i);
            }
            cal();
        }

    }

    protected abstract void autoDo(int i);

    protected abstract void cal() throws IOException;

    public void doByEvent(MessageEvent messageEvent, String msg, long senderId, boolean isGroup) throws Exception {
        if (state != STATE.PLAYING) throw new Exception("游戏未开始");
        int id = getIndexById(senderId);
        if (id == -1) throw new Exception("你不在游戏中");
    }

    /**
     * 自动使用预设参数
     */
    public void showInfo() throws IOException {
        showInfo(players.size() + 1, round + 1);
    }


    /**
     * 设定行数和列树
     */
    public void showInfo(int col, int row) throws IOException {
        showInfo(col, row, myHighLight, myFirstColString);
    }

    /**
     * 设定行数和列树和高亮函数
     */
    public void showInfo(int col, int row, HighLightInterface highLightInterface, FirstColString firstColString) throws IOException {
        showInfo(info, col, row, width, height, gridMarginTop, gridMarginBottom, gridMarginLeft, gridMarginRight, firstColWidth, highLightInterface, firstColString);
    }

    /**
     * 返回被展示信息的图片文件地址
     * info为被展示信息
     * width和height为宽度和高度
     * gridMarginTop、gridMarginBottom、gridMarginLeft、gridMarginRight为边界
     * firstColWidth为第一列宽度
     * func为定制高亮
     */
    public void showInfo(String[][] info, int col, int row, int width, int height, int gridMarginTop, int gridMarginBottom, int gridMarginLeft, int gridMarginRight, int firstColWidth, CONSTANTS.HighLightInterface highLightInterface, FirstColString firstColString) throws IOException {
        int imageWidth = width * col + firstColWidth + gridMarginLeft + gridMarginRight;
        int imageHeight = height * row + gridMarginTop + gridMarginBottom;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        //画背景
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        graphics.setColor(textColor);
        graphics.setFont(infoFont);
        //画格子
        for (int i = 0; i < row; i++) {
            if (myHighLight.check(i)) {
                graphics.setColor(highLightColor);
                graphics.fillRect(gridMarginLeft, gridMarginTop + i * height, width * col + firstColWidth, height);
                graphics.setColor(textColor);
            }
            graphics.drawRect(gridMarginLeft, gridMarginTop + i * height, firstColWidth, height);
            graphics.drawString(myFirstColString.get(i), gridMarginLeft + 10, gridMarginTop + i * height + height / 2 + 10);
            for (int j = 0; j < players.size(); j++) {
                graphics.drawRect(gridMarginLeft + firstColWidth + j * width, gridMarginTop + i * height, width, height);
                graphics.drawString(info[i][j], gridMarginLeft + firstColWidth + j * width + 5, gridMarginTop + i * height + height / 2 + 10);
            }
        }

        File file = writeImageToFile(image, "TEMPS", name + group.getId() + ".png");

        ExternalResource externalResource = ExternalResource.create(file);
        Message message = group.uploadImage(externalResource);
        group.sendMessage(message);
        externalResource.close();
    }


    public long getGroupId() {
        return group.getId();
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getName() {
        return "-" + name;
    }

    public boolean isKillingGame() {
        return killingGame;
    }

    public void setKillingGame(boolean killingGame) {
        this.killingGame = killingGame;
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * 根据QQ号获得下标，返回-1为失败
     */

    public int getIndexById(long id) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == id) return i;
        }
        return -1;
    }

    public int getIndexByNickName(String nickName) {
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(players.get(i).getNickName(), nickName)) return i;
        }
        return -1;
    }

    protected void timer() {
        int time = 0;
        while (state == STATE.PREPARING && time < startTime) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            if (state == STATE.PREPARING && time == startTime - 30)
                group.sendMessage("游戏长时间没开始，30s后，自动结束本群的" + getName());

        }
        if (state == STATE.PREPARING) {
            this.kill();
            group.sendMessage("游戏长时间没开始，自动结束" + getName());
        }
    }

}
