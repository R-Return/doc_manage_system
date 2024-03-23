package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Myfile对象", description="")
public class Myfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    @TableField("userId")
    private Integer userid;

    @ApiModelProperty(value = "文件名")
    @TableField("fileName")
    private String filename;

    @ApiModelProperty(value = "文件地址")
    @TableField("fileUrl")
    private String fileurl;

    @ApiModelProperty(value = "文件标签")
    @TableField("label")
    private String label;


    public Myfile(Integer userid, String filename, String fileurl,String label) {
        this.userid = userid;
        this.filename = filename;
        this.fileurl = fileurl;
        this.label = label;
    }
}
