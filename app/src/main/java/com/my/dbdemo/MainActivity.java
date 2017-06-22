package com.my.dbdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.my.dbdemo.db.callback.OnDoTransaction;
import com.my.dbdemo.db.helper.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private DBHelper<Product> helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        helper = new DBHelper<>(this, Product.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                //批量插入做事务
                doTransactionBulkInsert();
                break;

            case R.id.btn2:
                //更新一个实体
                updateSingleEntity();
                break;

            case R.id.btn3:
                //通过一个实体类更新指定字段
                updateColumnsByEntity();
                break;

            case R.id.btn4:
                //通过id进行字段修改
                updateColumnsById();
                break;

            case R.id.btn5:
                //删除表中所有数据
                deleteTableAllData();
                break;

            case R.id.btn6:
                //通过id删除一行
                deleteSingleLineById();
                break;

            case R.id.btn7:
                //通过entity删除一行
                deleteSingleLineEntity();
                break;

            case R.id.btn8:
                //通过id查询一条数据
                selectById();
                break;

            case R.id.btn9:
                long result = helper.selectCount(null);
                Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 批量插入做事务
     */
    private void doTransactionBulkInsert() {
        final List<Product> productList = testData();
        helper.execute(new OnDoTransaction() {
            @Override
            public void doInTransaction() {
                for (Product product : productList) {
                    long lo = helper.insert(product);
                    Log.e("XXX", "" + lo);
                }
            }
        });
    }


    /**
     * 更新一个实体
     */
    private void updateSingleEntity() {
        Product p = new Product();
        p.setId(10L);
        p.setName("保时捷卡宴");
        p.setDate(1487832498948L);
        p.setProductId("1011");
        p.setPrice(2830000.0123456789f);
        p.setDou(888000.0123456789);
        p.setPhotoCount(99960);
        p.setInteger(668567890);
        p.setBeanExplainRemark("保时捷卡宴插电式混合动力现车Cayenne S E-Hybrid配备了绿色的刹车卡钳，并配备了265/50 R19的米其林Michelin Latitude Sport低滚动阻力轮胎。保时捷卡宴最早亮相于2002年初的日内瓦车展，分为Cayenne，Cayenne S，Cayenne Turbo，Cayenne Turbo S和Cayenne GTS五个类别。由于出身于以生产超级跑车著称的保时捷公司，卡宴虽然身为SUV，却也不可避免地带有许多跑车的特质。因此也成为世界上速度最快的越野车，成为了越野世界中的一个飞驰的“辣椒”。在西班牙语中，cayenne是“辣椒”的意思。而这款著名跑车生产厂家生产的越野车无论是外在还是内在，也确实像一支火辣辣的辣椒，吸引着人们的目光。");
        p.setIsSellOut(0);
        int result = helper.update(p);
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }


    /**
     * 通过一个实体类更新指定字段
     */
    private void updateColumnsByEntity() {
        Product p = new Product();
        p.setId(8L);
        p.setDou(0.0000003);
        p.setPrice(0.00000003f);
        p.setPhotoCount(999);
        int result = helper.update(p, "dou", "price", "photo_count");
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }


    /**
     * 通过id进行字段修改
     */
    private void updateColumnsById() {
        int result = helper.update(2, new String[]{"bean_explain_remark", "is_sell_out", "name"}, new String[]{"这是备注", "1", "通过id进行字段修改"});
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }


    /**
     * 删除表中所有数据
     */
    private void deleteTableAllData() {
        int result = helper.truncate();
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }


    /**
     * 通过id删除一行
     */
    private void deleteSingleLineById() {
        int result = helper.deleteById(10);
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }


    /**
     * 通过entity删除一行
     */
    private void deleteSingleLineEntity() {
        Product p = new Product();
        p.setId(1L);
        int result = helper.deleteByEntity(p);
        Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();
    }

    /**
     * 通过id查询一条数据
     */
    private void selectById() {
        Product p = helper.selectAll(1L);
        Log.e("XXX", "id = " + p.getId() + "，" + p.toString());
    }

    private void initView() {
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);


        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
    }

    /**
     * 假数据
     *
     * @return List<Product>
     */
    private List<Product> testData() {
        List<Product> list = new ArrayList<>();
        Product p1 = new Product();
        p1.setName("HTC View");
        p1.setDate(1490942719665L);
        p1.setProductId("1001");
        p1.setPrice(6888.0123456789f);
        p1.setDou(4999.0123456789);
        p1.setPhotoCount(1234567890);
        p1.setInteger(668567890);
        p1.setBeanExplainRemark("HTC View 是由HTC与Valve联合开发的一款VR头显（虚拟现实头戴式显示器）产品，于2015年3月在MWC2015上发布。由于有Valve的SteamVR提供的技术支持，因此在Steam平台上已经可以体验利用View功能的虚拟现实游戏。2016年6月，HTC推出了面向企业用户的View虚拟现实头盔套装—View BE（即商业版），其中包括专门的客户支持服务。");
        p1.setIsSellOut(0);

        Product p2 = new Product();
        p2.setName("Microsoft HoloLens");
        p2.setDate(1487832498948L);
        p2.setProductId("1002");
        p2.setPrice(20000.0123456789f);
        p2.setDou(28888.0123456789f);
        p2.setPhotoCount(99960);
        p2.setInteger(668567890);
        p2.setBeanExplainRemark("微软的HoloLens自公布以来，几乎获得了一边倒的赞美声。它使用了增强现实(AR)技术，在现实世界中叠加一层虚拟影像，能让人仿佛置身魔法世界。但从最新的Hololens硬件体验上看，它可能有一个致使缺陷：视野太窄。");
        p2.setIsSellOut(0);

        Product p3 = new Product();
        p3.setName("Galaxy S8");
        p3.setDate(1487917355180L);
        p3.setProductId("1003");
        p3.setPrice(5888.6123456789f);
        p3.setDou(7999.5883456789);
        p3.setPhotoCount(1774567890);
        p3.setInteger(668267890);
        p3.setBeanExplainRemark("HIFI音效，NFC，PDAF相位对焦，USB Type-C接口，大容量电池，防抖功能，防水防尘，加密保护功能，金属机身，快速充电，美颜自拍，无线充电，指纹识别，虹膜识别，双侧曲面屏");
        p3.setIsSellOut(0);

        Product p4 = new Product();
        p4.setName("特斯拉Model 3");
        p4.setDate(1488104187498L);
        p4.setProductId("1004");
        p4.setPrice(358866.0123456789f);
        p4.setDou(500000.0123456789);
        p4.setPhotoCount(1067890);
        p4.setInteger(649998889);
        p4.setBeanExplainRemark("美国时间3月31日，特斯拉Model 3正式发布，新车作为特斯拉的入门级车型，新车在美的起售价为35000美元，约合人民币22万元。新车目前已正式接受预定，国内消费者可以支付1000美金（约合人民币6464元）进行预订，新车预计最快在2017年交付。");
        p4.setIsSellOut(0);

        Product p5 = new Product();
        p5.setName("奥迪S8");
        p5.setDate(1490942856547L);
        p5.setProductId("1005");
        p5.setPrice(1599999.0123456789f);
        p5.setDou(1800000.0123456789);
        p5.setPhotoCount(123456788);
        p5.setInteger(668536890);
        p5.setBeanExplainRemark("奥迪S8是奥迪A8的运动性能版，全新一代奥迪S8只从新A8上针对外观及内饰的部分细节进行修改而来。外形设计上，奥迪S8并没有比普通版A8激进多少。动力方面，全新一代奥迪S8配备的是4.0TFSI双涡轮增压发动机，最大功率不低于520马力，在1700rpm-5500rpm时持续输出650N·m的扭矩。");
        p5.setIsSellOut(0);

        Product p6 = new Product();
        p6.setName("xBox");
        p6.setDate(1490943728659L);
        p6.setProductId("1006");
        p6.setPrice(2699.0123456789f);
        p6.setDou(3000.0123456789);
        p6.setPhotoCount(1234567890);
        p6.setInteger(668567590);
        p6.setBeanExplainRemark("Xbox和SONY的PS2，以及任天堂公司的NGC形成了三足鼎立的局面。内装英特尔公司制造的Pentium III基本中央处理器、内建8GB容量的硬盘与DVD-ROM光驱、以太网路连接埠，支援网络的能力，与个人电脑架构相似。控制器端口形状与USB规格不相同。从硬件性能指标上，Xbox属于当时的“三大主机”。Xbox Live是Xbox及其后的第二代占据现在市场主流的Xbox 360专用的多用户在线对战平台。");
        p6.setIsSellOut(0);

        Product p7 = new Product();
        p7.setName("MacBook Pro");
        p7.setDate(1490944443719L);
        p7.setProductId("1007");
        p7.setPrice(13888.0123456789f);
        p7.setDou(13888.0123456789);
        p7.setPhotoCount(1234567890);
        p7.setInteger(668567890);
        p7.setBeanExplainRemark("2.9GHz 双核 Intel Core i5 处理器  Turbo Boost 最高可达 3.3GHz 8GB 2133MHz 内存 256GB PCIe 固态硬盘1 Intel Iris Graphics 550 图形处理器 四个 Thunderbolt 3 端口 新款背光键盘 (中文-拼音) 与使用手册 (中文) Multi-Touch Bar 和 Touch ID");
        p7.setIsSellOut(0);

        Product p8 = new Product();
        p8.setName("任天堂红白机");
        p8.setDate(1490944666032L);
        p8.setProductId("1008");
        p8.setPrice(1599.01234567892f);
        p8.setDou(1599.012345670089);
        p8.setPhotoCount(1234567890);
        p8.setInteger(668867890);
        p8.setBeanExplainRemark("红白机Family Computer（简称FC或FAMICOM）是任天堂公司发行的第一代家用游戏机。自1983年推出到2003年停产为止，全球出货6291万台。根据外媒数据，在1990年30%的美国家庭都有NES主机。FC的下一代为任天堂SFC。");
        p8.setIsSellOut(0);

        Product p9 = new Product();
        p9.setName("Call Of Duty 10: Ghost");
        p9.setDate(1490945213744L);
        p9.setProductId("1009");
        p9.setPrice(49.0123456789f);
        p9.setDou(49.0123456789);
        p9.setPhotoCount(1255597890);
        p9.setInteger(668567890);
        p9.setBeanExplainRemark("《使命召唤：幽灵》 是由动视暴雪旗下工作室Infinity Ward于2013年发行的一款第一人称射击游戏，属于使命召唤系列第十作。");
        p9.setIsSellOut(0);

        Product p10 = new Product();
        p10.setName("语文 高中二年级 上册");
        p10.setDate(1490945897487L);
        p10.setProductId("1010");
        p10.setPrice(30.0123456789f);
        p10.setDou(40.0123456789);
        p10.setPhotoCount(444337890);
        p10.setInteger(668567890);
        p10.setBeanExplainRemark("傻逼高中语文书");
        p10.setIsSellOut(1);

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p8);
        list.add(p9);
        list.add(p10);

        return list;
    }
}
