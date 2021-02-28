package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/***
 * 因为在新增商品时，需要 spu、sku 的信息，
 * 而且 sku 是集合的形式，所以，我们封装一个 Javabean ，
 * 把 spu 和 List<sku> 作为属性
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    public Goods(Spu spu, List<Sku> skuList) {
        this.spu = spu;
        this.skuList = skuList;
    }

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
