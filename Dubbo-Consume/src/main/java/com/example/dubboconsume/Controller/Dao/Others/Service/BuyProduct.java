package com.example.dubboconsume.Controller.Dao.Others.Service;

import com.example.dubboconsume.Controller.Dao.ConsumeImp;
import com.example.dubboconsume.Controller.Dao.Others.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BuyProduct {
    @Autowired
    GeneratePrice generatePrice;

    @Autowired
    UserCreate userCreate;

    @Autowired
    ConsumeImp consume;
    BuyProduct(){}
    User user;
    public IsBuy buyProduct() throws JsonProcessingException {
        user = userCreate.generateRandomUser();//随机生成用户
        List<Coupon> coupons = getCoupon(user);//调用接口获取优惠券
        int discount=0;
        int total=0;
        // 获取当前时间--推荐时间
        Date currentDate = new Date();
        // 格式化为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String recommenddDate = dateFormat.format(currentDate);
        String buyDate=null;

        boolean ifBuy;

        int price = returnPrice();
        Coupon coupon = getBestCoupon(coupons,price);
        if(coupon==null){
            ifBuy = false;
        }else{
            //        这里是优惠券金额
            discount=coupon.getDiscount();//优惠券金额,选择推荐的其中之一来推荐金额
            total=coupon.getTotal();//总金额
            double habitDiscount = user.getHabit().getHabitDiscount();
            double chae=(total-discount)/total;

            if (chae>=habitDiscount){
                ifBuy=true;
            }else{
                ifBuy=false;
            }
            // 获取当前时间--购买时间
            currentDate = new Date();
            // 格式化为字符串
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            buyDate = dateFormat.format(currentDate);
        }

        IsBuy isBuy = new IsBuy(ifBuy,price,discount,recommenddDate,buyDate);
        return isBuy;
    }
    //    调用接口,返回优惠券信息
    public List<Coupon> getCoupon(User user) throws JsonProcessingException {
        return consume.getCoupons(user);
    }
    //    获取满足价格条件的最佳的优惠券，没有则返回null，之后再去判断最佳的是否满足消费习惯
    public Coupon getBestCoupon(List<Coupon> coupons, int price){
        Coupon bestCoupon=null;
        for(Coupon coupon:coupons){
            int total = coupon.getTotal();
            if(total>price){
                continue;
            }else{
                if(bestCoupon==null){
                    bestCoupon=coupon;
                }else if(bestCoupon.getDiscount()<coupon.getDiscount()){
                    bestCoupon=coupon;
                }
            }
        }
        return bestCoupon;
    }
    public int returnPrice(){
        user=userCreate.generateRandomUser();
        generatePrice.setRangeOfHobby(user.getHobby().getPriceRange());
        generatePrice.setRangeOfOcupation(user.getOcupation().getPriceRange());
        return generatePrice.generatePrice();
    }

}
