package com.healthyForum.repository.Group;

import com.healthyForum.model.Group.GroupPost;
import com.healthyForum.model.Group.GroupPostComment;
import com.healthyForum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPostCommentRepository extends JpaRepository<GroupPostComment, Long> {

    // Find comments by post
    List<GroupPostComment> findByPostOrderByCreatedAtAsc(GroupPost post);

    // Find recent comments for a post
    @Query("SELECT c FROM GroupPostComment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<GroupPostComment> findRecentCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // Find user's comments in a specific group
    @Query("SELECT c FROM GroupPostComment c WHERE c.author = :author AND c.post.group.id = :groupId ORDER BY c.createdAt DESC")
    List<GroupPostComment> findByAuthorAndGroupId(@Param("author") User author, @Param("groupId") Long groupId);


    @Query("SELECT c FROM GroupPostComment c " +
            "JOIN FETCH c.author a " +
            "LEFT JOIN FETCH a.account acc " +
            "WHERE c.post.id = :postId " +
            "ORDER BY c.createdAt ASC")
    List<GroupPostComment> findByPostIdWithAuthor(@Param("postId") Long postId);
}