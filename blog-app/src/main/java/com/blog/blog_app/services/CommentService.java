package com.blog.blog_app.services;

import com.blog.blog_app.dto.CommentDto;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto,Integer postId);
    void deleteComment(Integer commentId);
}
