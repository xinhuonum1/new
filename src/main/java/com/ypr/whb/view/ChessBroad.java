package com.ypr.whb.view;

import com.ypr.whb.connect.DataSocket;
import com.ypr.whb.data.GameCenter;
import com.ypr.whb.data.Player;
import com.ypr.whb.data.Spot;
import com.ypr.whb.data.TableData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 界面显示 之 棋桌面板，显示棋盘，绘制棋子
 * *
 * 当界面重新被加载时，无法正常绘制出所有棋子！(加入线程后解决问题！)
 * *
 * 同样的，棋桌间断性地绘制失败(加入线程后解决问题！)
 * *
 * 将胜负判断加在绘制棋子里面则导致，游戏结束后，界面重新绘制时，导致多次判断胜负
 * *
 * 因此，新增一个函数，用于判断提交的绘制请求是否符合要求
 * *
 * 绘制圆形fillOval,指定的位置是左顶点的位置！
 * *
 * 新增绘制五子连珠情况
 * *
 * 主界面重新绘制后，棋盘绘制不完整(加入线程,等待棋盘绘制完成,等待棋子绘制完成,再绘制结果)
 * @author 帝睿
 */

public class ChessBroad extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 棋子大小
     */
    static int chessSize;

    static ChessBroad myBroad;

    /**
     * 胜利的条件
     */
    private static int victory=5;

    //利用单线程池的fifo，达到线程池内线程顺序执行。

    private static ExecutorService threadPool;

    ChessBroad() {
        threadPool= Executors.newSingleThreadExecutor();
        this.setVisible(true);
        // 设置鼠标形状为手型
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setBackground(new Color(249, 214, 91));
        this.addListener();
        myBroad = this;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintChessBroad();
        paintResult();
    }

    /**
     * 界面显示，控件加载完毕后执行
     */
    static void init() {
        chessSize = myBroad.getWidth() / TableData.spotNum;
        paintChessBroad();
        myBroad.repaint();
    }

    private static void addGrade() {
        // 当前应该下白棋，则黑棋获胜
        if (!Player.myPlayer.getColor().equals(TableData.getNowColor())) {
            int grade = Player.myPlayer.addGrade(100);
            UserPanel.setGrade(grade, UserPanel.left);
        } else {
            int grade = Player.otherPlayer.addGrade(100);
            UserPanel.setGrade(grade, UserPanel.right);
        }
    }

    /**
     * 添加监听事件
     */
    private void addListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int cx = e.getX();
                int cy = e.getY();
                System.out.print("点击坐标" + cx + ":" + cy);

                // 根据坐标，获得行列
                int row = Coordinate.yToRow(cy);
                int col = Coordinate.xToCol(cx);
                Spot spot = new Spot(row - 1, col - 1, TableData.getNowColor());
                System.out.println("， 点击行列" + row + ":" + col);

                boolean canSpot = isCanSpot(spot);
                if (canSpot) {
                    if (GameCenter.getMode() == GameCenter.MODE_ONLINE) {
                        DataSocket.send(spot);
                    }
                    submitPaint(spot);
                }
            }
        });
    }

    /**
     * 接收绘制棋子请求，先验证，后绘制
     */
    public static void submitPaint(Spot spot) {
        paintChessImages(spot);
        TableData.putDownChess(spot);

        if (TableData.isOver()) {
            addGrade();// 增加用户积分
            GameCenter.setMode(GameCenter.MODE_END);
            paintResult();
            if (Spot.whiteChess.equals(TableData.getNowColor())) {
                // 当前应该下白棋，则黑棋获胜
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "黑棋获胜！增加100积分！",
                        "游戏结束", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "白棋获胜！增加100积分！",
                        "游戏结束", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private static boolean isCanSpot(Spot spot) {
        if (TableData.hasSpot(spot.getRow(), spot.getCol())) {
            System.out.println("ChessBroad 此点已有棋子！");
            return false;
        }
        //如果时联机对战，界面上只能下自己颜色的棋子
        if (GameCenter.getMode() == GameCenter.MODE_ONLINE) {
            if (!spot.getColor().equals(Player.myPlayer.getColor())) {
                JOptionPane.showMessageDialog(MainFrame.mainFrame,
                        "联机对战中，请先等待对方下棋", "请等待..",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println(Player.myPlayer.getColor() + ":" + TableData.getNowColor());
                return false;
            }
        }

        // 判断符合下棋要求
        if (GameCenter.isEnd()) {
            JOptionPane.showMessageDialog(null,
                    "游戏未开始，或已结束！\n请选择游戏模式，以开始游戏，", "游戏未开始",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 绘制单颗棋子,执行则绘制棋子，不判断棋子的正确性
     *
     */
    private static void paintChessImages(Spot spot) {
        if (spot != null) {
            int row = spot.getRow() + 1;
            int col = spot.getCol() + 1;

            int cx = Coordinate.colToX(col);
            int cy = Coordinate.rowToY(row);
            Graphics g = myBroad.getGraphics();
            String color = spot.getColor();
            switch (color) {
                case Spot.blackChess:
                    g.setColor(Color.black);
                    break;
                case Spot.whiteChess:
                    g.setColor(Color.white);
                    break;
                default:
                    return;
            }
            g.fillOval(cx - chessSize / 2, cy - chessSize / 2, chessSize,
                    chessSize);
        }
    }

    /**
     * 绘制整个棋桌
     */
    private static void paintChessBroad() {
        final Graphics graphics = myBroad.getGraphics();
        graphics.setFont(new Font("黑体", Font.BOLD, 20));
        threadPool.execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < TableData.spotNum; i++) {
                graphics.drawString("" + (i + 1), 0, 20 + chessSize * i);
                graphics.drawLine(chessSize / 2, chessSize / 2 + chessSize * i,
                        chessSize / 2 + chessSize * 18, chessSize / 2
                                + chessSize * i);

                graphics.drawString("" + (i + 1), chessSize * i + 8, 15);
                graphics.drawLine(chessSize / 2 + chessSize * i, chessSize / 2,
                        chessSize / 2 + chessSize * i, chessSize / 2
                                + chessSize * 18);
            }
            for (int i = 0; i < TableData.spotNum; i++) {
                for (int j = 0; j < TableData.spotNum; j++) {
                    Spot spot = TableData.getSpot(i, j);
                    paintChessImages(spot);
                }
            }
        });
    }

    /**
     * 绘制赢棋后的五子连珠状况
     */
    private static void paintResult() {
        threadPool.execute(() -> {
            if (GameCenter.isEnd()) {
                if (TableData.endRow + TableData.endCol < victory) {
                    return;
                }

                System.out.println("赢棋起始位置：" + TableData.indexRow + " " + TableData.indexCol);
                System.out.println("赢棋终止位置：" + TableData.endRow + " " + TableData.endCol);

                Graphics2D g = (Graphics2D) myBroad.getGraphics();
                int indexX = Coordinate.colToX(TableData.indexCol + 1);
                int indexY = Coordinate.rowToY(TableData.indexRow + 1);
                int endX = Coordinate.colToX(TableData.endCol + 1);
                int endY = Coordinate.rowToY(TableData.endRow + 1);
                g.setColor(Color.yellow);
                g.setStroke(new BasicStroke(5.0f));
                g.setFont(new Font("黑体", Font.BOLD, 150));
                g.drawLine(indexX, indexY, endX, endY);
            }
        });

    }
}
