package com.ypr.whb.connect;

import com.ypr.whb.data.Player;
import com.ypr.whb.data.Spot;
import com.ypr.whb.view.ChatRoom;
import com.ypr.whb.view.ChessBroad;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据发送方案,数据接收，对外接口
 *
 * 3种消息类型，三种不同编码规则
 * 第一个位置放消息类型
 *
 */
public class DataSocket {
    private final static String TYPE_chat = "chat";
    private final static String TYPE_spot = "spot";
    private final static String TYPE_player = "player";

    /**
     * 发送消息
     * @param object
     */
    public static void send(Object object) {

        List<String> list = new ArrayList<>();

        if (object instanceof Spot) {
            sendSpotMessage(list,object);
        } else if (object instanceof Player) {
           sendPlayerMessage(list,object);
        }else if (object instanceof String) {
            sendChatMessage(list,object);
        }
    }

    /**
     * 发送各种类型消息
     * @param list
     * @param object
     */
    private static void sendSpotMessage(List<String> list, Object object){
        list.add(TYPE_spot);
        int row = ((Spot) object).getRow();
        int col = ((Spot) object).getCol();
        //保持数据格式一致性，为两位数，10以内为01，02诸如此类
        if(row < 10){
            list.add("0" + row);
        }else{
            list.add("" + row);
        }

        if(col < 10){
            list.add("0" + col);
        }else{
            list.add("" + col);
        }

        MySocket.sendData(list);
    }

    private static void sendPlayerMessage(List<String> list, Object object){
        Player player = (Player) object;
        list.add(TYPE_player);
        list.add(player.getName());
        list.add("" + player.getGrade());
        MySocket.sendData(list);
    }

    private static void sendChatMessage(List<String> list, Object object){
        list.add(TYPE_chat);
        list.add((String) object);
        MySocket.sendData(list);
    }
    /**
     * 接收数据，数据处理后显示
     */
    public static void receive(List<String> list) {
        String str = list.get(0);
        switch (str) {
            case TYPE_chat:
                receiveChatMessage(list);
                break;
            case TYPE_spot:
                receiveSpotMessage(list);
                break;
            case TYPE_player:
                receivePlayerMessage(list);
                break;
            default:
                System.out.println("DataSocket 收到未知数据！" + str);
                break;
        }
    }

    private static void receiveChatMessage(List<String> list){
        ChatRoom.addText(list.get(1), ChatRoom.HE_TEXT);
    }
    private static void receiveSpotMessage(List<String> list){
        int row = Integer.parseInt(list.get(1));
        int col = Integer.parseInt(list.get(2));
        String color = Player.otherPlayer.getColor();
        ChessBroad.submitPaint(new Spot(row, col, color));
    }
    private static void receivePlayerMessage(List<String> list){
        String name = list.get(1);
        int grade = Integer.parseInt(list.get(2));
        Player.otherPlayer.setName(name);
        Player.otherPlayer.setGrade(grade);
    }
}
