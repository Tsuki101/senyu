package com.lizhi.xiaohashu.kv.biz.service;
import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.kv.dto.req.AddNoteContentReqDTO;
import com.lizhi.xiaohashu.kv.dto.req.DeleteNoteContentReqDTO;
import com.lizhi.xiaohashu.kv.dto.req.FindNoteContentReqDTO;
import com.lizhi.xiaohashu.kv.dto.rsp.FindNoteContentRspDTO;

/**

 * @description: 笔记内容存储业务
 **/
public interface NoteContentService {

    /**
     * 添加笔记内容
     *
     * @param addNoteContentReqDTO
     * @return
     */
    Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO);
    Response<FindNoteContentRspDTO> findNoteContent(FindNoteContentReqDTO findNoteContentReqDTO);
    Response<?> deleteNoteContent(DeleteNoteContentReqDTO deleteNoteContentReqDTO);
}

