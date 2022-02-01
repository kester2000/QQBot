package org.Kester.QQBot.Games;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {
    /**
     * 游戏玩家类，存放玩家数据
     * id为玩家QQ号，nickName为2-6位支持中英文(包括全角字符)、数字、下划线和减号，其中中文2位，其他1位
     */
    private long id;
    private String nickName;

    public Player(long id, String nickName) throws Exception {
        this.id = id;
        setNickName(nickName);
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
        if (!validateUserName(nickName)) throw new Exception( "不允许出现特殊字符");
        if (getStrLength(nickName) < 2) throw new Exception( "昵称过短");
        if (getStrLength(nickName) > 6) throw new Exception( "昵称过长");
        this.nickName = nickName;
    }
}
