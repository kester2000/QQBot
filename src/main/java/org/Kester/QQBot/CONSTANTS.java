package org.Kester.QQBot;

import java.awt.*;

public class CONSTANTS {
    public static Color backgroundColor = new Color(255, 255, 255);  // 棋盘背景色
    public static Color textColor = new Color(0, 0, 0);  // 棋盘线色
    public static Color highLightColor = new Color(255, 255, 0); // 高亮色
    //private static Font indexFont = new Font("微软雅黑", Font.BOLD, 25);
    public static Font titleFont = new Font("微软雅黑", Font.BOLD, 20);
    public static Font infoFont = new Font("宋体", Font.ROMAN_BASELINE, 15);
    public static int KILLGAMETIME = 180;

    public enum STATE {
        STOPPING,
        PREPARING,
        PREPARED,
        PLAYING,
    }

    public interface HighLightInterface {
        boolean check(int param1);
    }

    public interface FirstColString {
        String get(int param1);
    }
}
