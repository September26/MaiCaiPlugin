package com.xt.qc.model;

import java.util.ArrayList;
import java.util.List;

public class NodeModel {

    public String className = "";//控件名称
    public String text = "";//控件显示内容
    public List<NodeModel> nextNodeList = new ArrayList<>();
}
