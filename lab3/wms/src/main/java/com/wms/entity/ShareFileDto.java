package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@Data
public class ShareFileDto implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;


    private Integer userid;


    private String filename;


    private String fileurl;


    private String label;


    private Integer shareFrom;

    private String userName;
}
