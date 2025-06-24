package com.lizhi.xiaohashu.note.biz.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublishNoteReqVO {

    @NotNull(message = "笔记类型不能为空")
    private Integer type;

    private List<String> imgUris;

    private String videoUri;

    private String title;

    private String content;

    private Long topicId;
}
/*{
        "type": 0, // 笔记类型，0 代表图文笔记，1 代表视频笔记
        "imgUris": [
        "http://116.62.199.48:9000/weblog/c89cc6b66f0341c0b7854771ae063eac.jpg"
        ], // 图片链接数组，当为图文笔记时，此字段不能为空
        "videoUri": "http://xxxx", // 视频连接，当为视频笔记时，此字段不能为空
        "title": "图文笔记测试标题", // 笔记标题
        "content": "图文笔记测试内容", // 笔记内容（可不填）
        "topicId": 1 // 话题 ID（可不填）
        }*/
