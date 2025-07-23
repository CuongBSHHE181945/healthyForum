package com.healthyForum.service.Group;

import com.healthyForum.model.Group.Group;
import com.healthyForum.model.Group.GroupPost;
import com.healthyForum.model.Group.GroupPostComment;
import com.healthyForum.model.User;
import com.healthyForum.repository.Group.GroupPostCommentRepository;
import com.healthyForum.repository.Group.GroupPostRepository;
import com.healthyForum.repository.Group.GroupRepository;
import com.healthyForum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GroupPostService {

    @Autowired
    private GroupPostRepository groupPostRepository;

    @Autowired
    private GroupPostCommentRepository groupPostCommentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupMemberService groupMemberService;

    // ===== GROUP POST METHODS =====

    // Update create post method
    public GroupPost createPost(Long groupId, String title, String content,
                                MultipartFile imageFile, String imageUrl, Principal principal) {
        User author = userService.getCurrentUser(principal);
        if (author == null) {
            throw new RuntimeException("User not found");
        }

        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!groupMemberService.isMember(groupId, author)) {
            throw new RuntimeException("User must be a member to create posts");
        }

        GroupPost post = new GroupPost(title, content, group, author);

        // Handle image upload
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadedImageUrl = uploadImage(imageFile);
                post.setImg(uploadedImageUrl);
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                post.setImg(imageUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload hình ảnh: " + e.getMessage());
        }

        return groupPostRepository.save(post);
    }


    // Get posts by group with pagination
    @Transactional(readOnly = true)
    public Page<GroupPost> getGroupPosts(Long groupId, Pageable pageable) {
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return groupPostRepository.findByGroupAndIsActiveTrueOrderByCreatedAtDesc(group, pageable);
    }


    // Delete post
    public void deletePost(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GroupPost post = groupPostRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is author or has admin/moderator role
        if (!post.getAuthor().getId().equals(user.getId()) &&
                !groupMemberService.hasAdminOrModeratorRole(post.getGroup(), user)) {
            throw new RuntimeException("You don't have permission to delete this post");
        }

        post.setIsActive(false);
        groupPostRepository.save(post);
    }

    // Like post
    public void likePost(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GroupPost post = groupPostRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.incrementLike();
        groupPostRepository.save(post);
    }


    // ===== COMMENT METHODS =====

    // Create comment
    public GroupPostComment createComment(Long postId, String content, Principal principal) {
        User author = userService.getCurrentUser(principal);
        if (author == null) {
            throw new RuntimeException("User not found");
        }

        GroupPost post = groupPostRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is member of the group
        if (!groupMemberService.isMember(post.getGroup().getId(), author)) {
            throw new RuntimeException("User must be a member to comment");
        }

        GroupPostComment comment = new GroupPostComment(content, post, author);
        comment = groupPostCommentRepository.save(comment);

        // Update comment count
        post.incrementComment();
        groupPostRepository.save(post);

        return comment;
    }

    // Fix method getCommentsByPost
    @Transactional(readOnly = true)
    public List<GroupPostComment> getCommentsByPost(Long postId) {
        GroupPost post = groupPostRepository.findByIdAndIsActiveTrue(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Eager fetch để tránh lazy loading issues
        return groupPostCommentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    @Transactional(readOnly = true)
    public Optional<GroupPost> getPostById(Long postId) {
        return groupPostRepository.findByIdAndIsActiveTrueWithFullDetails(postId);
    }


    // Delete comment
    public void deleteComment(Long commentId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GroupPostComment comment = groupPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user is author or has admin/moderator role
        if (!comment.getAuthor().getId().equals(user.getId()) &&
                !groupMemberService.hasAdminOrModeratorRole(comment.getPost().getGroup(), user)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        GroupPost post = comment.getPost();
        groupPostCommentRepository.delete(comment);

        // Update comment count
        post.decrementComment();
        groupPostRepository.save(post);
    }



    // Get recent comments
    @Transactional(readOnly = true)
    public Page<GroupPostComment> getRecentComments(Long postId, Pageable pageable) {
        return groupPostCommentRepository.findRecentCommentsByPostId(postId, pageable);
    }

    // Get user's comments in group
    @Transactional(readOnly = true)
    public List<GroupPostComment> getUserCommentsInGroup(User user, Long groupId) {
        return groupPostCommentRepository.findByAuthorAndGroupId(user, groupId);
    }

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only accept image files");
        }

        // Validate file size (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File must not exceed 5MB");
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + filename;
    }

}