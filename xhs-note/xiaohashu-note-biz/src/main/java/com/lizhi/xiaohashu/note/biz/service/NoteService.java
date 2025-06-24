package com.lizhi.xiaohashu.note.biz.service;

import com.lizhi.framework.common.response.Response;
import com.lizhi.xiaohashu.note.biz.model.vo.FindNoteDetailReqVO;
import com.lizhi.xiaohashu.note.biz.model.vo.FindNoteDetailRspVO;
import com.lizhi.xiaohashu.note.biz.model.vo.PublishNoteReqVO;
import com.lizhi.xiaohashu.note.biz.model.vo.UpdateNoteReqVO;

public interface NoteService {

    /**
     * 笔记发布
     * @param publishNoteReqVO
     * @return
     */
    Response<?> publishNote(PublishNoteReqVO publishNoteReqVO);
    /**
     * 笔记详情
     * @param findNoteDetailReqVO
     * @return
     */
    Response<FindNoteDetailRspVO> findNoteDetail(FindNoteDetailReqVO findNoteDetailReqVO);
    //笔记更新
    Response<?> updateNote(UpdateNoteReqVO updateNoteReqVO);

}