package com.hqwx.bigdata.client.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: BDPage
 * @Author: XuShengBin
 * @Date: 2022-09-06
 * @Ver: v1.0 -create
 */
public class BDPage<T> implements Serializable ,Cloneable {
    protected List<T> records;
    protected long total;
    protected long size;
    protected long current;
}
