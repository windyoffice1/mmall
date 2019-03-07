package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        //检查用户名是否存在
       int userNameCount= userMapper.checkUsername(username);
       if(userNameCount==0){
           return  ServerResponse.createByErrorMsg("用户名不存在!");
       }
       //TODO 密码MD5加密
        User user=userMapper.selectLogin(username,MD5Util.MD5EncodeUtf8(password));
       if (user==null){
           return ServerResponse.createByErrorMsg("密码错误!");
       }
       user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //检查用户名是否存在
        ServerResponse validResopnse=this.checkValid(user.getUsername(),Const.USER_NAME);
        if(!validResopnse.ifsuccess()){
            return  validResopnse;
        }
        //检查邮箱是否存在
        validResopnse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResopnse.ifsuccess()){
            return  validResopnse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0) {
            return ServerResponse.createByErrorMsg("注册失败");
        }
        return ServerResponse.createBySuccessMsg("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if(StringUtils.isNotBlank(type )){
            if(Const.USER_NAME.equals(type)){
                //检查用户名是否存在
                int resultCount= userMapper.checkUsername(str);
                if(resultCount>0){
                    return  ServerResponse.createByErrorMsg("用户名已存在!");
                }
            }
            if(Const.EMAIL.equals(type)){
                //检查邮箱是否存在
                int resultCount= userMapper.checkEmail(str);
                if(resultCount>0){
                    return  ServerResponse.createByErrorMsg("邮箱已存在!");
                }
            }
        }else{
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    public ServerResponse selectQuestion(String username){

        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if(validResponse.ifsuccess()){
            //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMsg("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if(validResponse.ifsuccess()){
            //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token无效或者过期");
        }

        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password  = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMsg("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMsg("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMsg("密码更新成功");
        }
        return ServerResponse.createByErrorMsg("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的.
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMsg("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMsg("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMsg("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }
}
