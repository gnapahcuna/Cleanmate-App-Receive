package nl.psdcompany.duonavigationdrawer.example;

/**
 * Created by anucha on 2/10/2018.
 */

public class MyItemProduct {
    private String mBarcode;
    private String mBranch;
    private String mIsSelected;

    public MyItemProduct(String barcode, String branch,String selected) {
        mBarcode=barcode;
        mBranch=branch;
        mIsSelected=selected;
    }
    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }
    public String getBarcode() {
        return mBarcode;
    }
    public void setBranch(String branch) {
        mBranch = branch;
    }
    public String getBranch() {
        return mBranch;
    }
    public void setSelected(String selected) {
        mIsSelected = selected;
    }
    public String getSelected() {
        return mIsSelected;
    }
}
