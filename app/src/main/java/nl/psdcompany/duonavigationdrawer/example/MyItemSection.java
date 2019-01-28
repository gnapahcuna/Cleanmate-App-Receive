package nl.psdcompany.duonavigationdrawer.example;

/**
 * Created by anucha on 2/10/2018.
 */

public class MyItemSection {
    private String mOrderNo;

    public MyItemSection(String OrderNo) {
        mOrderNo=OrderNo;
    }
    public void setOrderNo(String orderno) {
        mOrderNo = orderno;
    }
    public String getOrderNo() {
        return mOrderNo;
    }
}
