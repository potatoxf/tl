package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import pxf.tl.help.Safe;
import pxf.tlx.basesystem.data.BaseTable;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "链接")
public class AccessLink extends BaseTable<Integer, AccessLink> {
    @ApiModelProperty(value = "主机地址")
    private String host;
    @ApiModelProperty(value = "主机端口")
    private Integer port;
    @ApiModelProperty(value = "主机URL")
    private String path;
    @ApiModelProperty(value = "访问类型")
    private String methodType;

    public String getUrl() {
        if (host != null) {
            return Safe.formatUrl(null, host, port, path);
        } else {
            return path;
        }
    }
}