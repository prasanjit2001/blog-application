package com.blog.blog_app.repositories;

import com.blog.blog_app.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {

}

