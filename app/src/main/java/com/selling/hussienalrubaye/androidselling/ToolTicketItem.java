package com.selling.hussienalrubaye.androidselling;

/**
 * Created by hussienalrubaye on 12/19/15.
 */
public class ToolTicketItem {
    public String ToolID;
    public String ToolName;
    public String ToolDes;
    public String ToolPrice;
    public  String DateAdd;
    public  String PictureLink;
    ToolTicketItem(String ToolID,String ToolName,String ToolDes,String ToolPrice,String DateAdd,
    String PictureLink){
        this.ToolID=ToolID;
        this.ToolName=ToolName;
        this.ToolDes=ToolDes;
        this.ToolPrice=ToolPrice;
        this.DateAdd=DateAdd;
        this.PictureLink=PictureLink;
    }
}
