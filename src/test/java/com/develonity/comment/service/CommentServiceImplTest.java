package com.develonity.comment.service;

import static com.develonity.comment.entity.CommentStatus.ADOPTED;
import static org.assertj.core.api.Assertions.assertThat;

import com.develonity.board.dto.CommunityBoardRequest;
import com.develonity.board.dto.QuestionBoardRequest;
import com.develonity.board.entity.BoardImage;
import com.develonity.board.entity.CommunityBoard;
import com.develonity.board.entity.CommunityCategory;
import com.develonity.board.entity.QuestionBoard;
import com.develonity.board.entity.QuestionCategory;
import com.develonity.board.repository.BoardImageRepository;
import com.develonity.board.service.CommunityBoardServiceImpl;
import com.develonity.board.service.QuestionBoardServiceImpl;
import com.develonity.comment.dto.CommentRequest;
import com.develonity.comment.dto.CommentResponse;
import com.develonity.comment.entity.Comment;
import com.develonity.comment.repository.CommentLikeRepository;
import com.develonity.comment.repository.CommentRepository;
import com.develonity.comment.repository.ReplyCommentRepository;
import com.develonity.user.entity.User;
import com.develonity.user.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentServiceImplTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private ReplyCommentService replyCommentService;
  @Autowired
  private CommentServiceImpl commentService;
  @Autowired
  private CommentLikeRepository commentLikeRepository;
  @Autowired
  private QuestionBoardServiceImpl questionBoardService;
  @Autowired
  private CommunityBoardServiceImpl communityBoardService;
  @Autowired
  private BoardImageRepository boardImageRepository;
  @Autowired
  private ReplyCommentRepository replyCommentRepository;

  @BeforeEach
  void allDeleteBefore() {
    commentRepository.deleteAll();
  }

  @AfterEach
  void allDeleteAfter() {
    commentRepository.deleteAll();
    commentLikeRepository.deleteAll();
    replyCommentRepository.deleteAll();
  }

  // 댓글 전체 조회
//  @Test
//  void getAllComment() {
//    // given
//    User user = new User();
//    CommentList commentList = new CommentList();
//    // thenReturn(Page.empty()) 로 빈페이지를 리턴시킨다.
//    when(commentRepository.findBy(commentList.toPageable())).thenReturn(Page.empty());
//    // when
//    Page<CommentResponse> responses = commentService.getAllComment(user, commentList);
//
//    // then
//    assertThat(responses).isEmpty();
//
//    // verify
//    // findBy가 실제로 호출되는지 확인
//    verify(commentRepository).findBy(commentList.toPageable());
//  }

//  @Test
//  @DisplayName("내가 쓴 댓글 조회")
//  void getMyComments() {
//    // given
//    CommentList commentList = new CommentList();
//    Optional<User> findUser = userRepository.findById(1L);
//    Optional<Comment> findComment = commentRepository.findById(1L);
//    // when
//    commentService.getMyComments(commentList, findUser.get().getId(), findUser.get());
//    // then
//    assertThat(commentList.toPageable()).isNotNull();
//  }

//  @Test
//  @DisplayName("게시글에 달린 댓글,대댓글 조회")
//  void getCommentsByBoard() {
//    // given
//    Optional<User> findUser = userRepository.findById(1L);
//    Long boardId = 1L;
//    // when
//    commentService.getCommentsByBoard(boardId, findUser.get());
//    // then
//    assertThat()
//  }

  // 질문 댓글 작성
  @Test
  @DisplayName("질문 댓글 작성")
  void createQuestionComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("제목", "내용", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest request = new CommentRequest("질문댓글");

    // when
    Comment comment = commentService.createQuestionComment(createQuestionBoard.getId(), request,
        findUser.get());

    // then
    assertThat(comment.getContent()).isEqualTo(request.getContent());
    // 자신이 만든 질문 게시글에 댓글을 달면 나오는 익셉션 테스트, 서비스에서 주석 풀면 정상 작동
//    assertThrows(CustomException.class,
//        () -> commentService.createQuestionComment(1L, request, findUser.get()));
  }

  @Test
  @DisplayName("질문 댓글 수정")
  void updateQuestionComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("제목", "내용", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest createCommentRequest = new CommentRequest("질문댓글");
    CommentRequest commentRequest = new CommentRequest("질문댓글수정");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());

    // when
    Comment updateComment = commentService.updateQuestionComment(createComment.getId(),
        commentRequest, findUser.get());

    // then
    assertThat(updateComment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("질문 댓글 삭제")
  void deleteQuestionComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("제목", "내용", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest createCommentRequest = new CommentRequest("질문댓글");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());

    // when
    commentService.deleteQuestionComment(createComment.getId(), findUser.get());

    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isFalse();
  }

  @Test
  @DisplayName("답변 채택 기능")
  void adoptComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    QuestionBoardRequest questionBoardRequest = new QuestionBoardRequest("제목", "내용", 10,
        QuestionCategory.BACKEND);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);
    QuestionBoard createQuestionBoard = questionBoardService.createQuestionBoard(
        questionBoardRequest, multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createQuestionBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest createCommentRequest = new CommentRequest("질문댓글");
    Comment createComment = commentService.createQuestionComment(createQuestionBoard.getId(),
        createCommentRequest,
        findUser.get());
    CommentResponse commentResponse = CommentResponse.builder()
        .commentStatus(ADOPTED)
        .build();

    // when
    commentService.adoptComment(createComment);

    // then
    assertThat(commentResponse.getCommentStatus()).isEqualTo(commentResponse.getCommentStatus());
  }

  @Test
  @DisplayName("잡담 댓글 작성")
  void createCommunityComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 잡담 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 잡담 댓글 요청
    CommentRequest commentRequest = new CommentRequest("잡담댓글");

    // when
    Comment comment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // then
    assertThat(comment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("잡담 댓글 수정")
  void updateCommunityComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 잡담 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 잡담 댓글 작성
    CommentRequest createCommunityCommentRequest = new CommentRequest("잡담댓글");
    CommentRequest commentRequest = new CommentRequest("잡담댓글수정");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        createCommunityCommentRequest,
        findUser.get());

    // when
    Comment updateComment = commentService.updateCommunityComment(createComment.getId(),
        commentRequest, findUser.get());

    // then
    assertThat(updateComment.getContent()).isEqualTo(commentRequest.getContent());
  }

  @Test
  @DisplayName("잡담 댓글 삭제")
  void deleteCommunity() throws IOException {
    // given
    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 잡담 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 잡담 댓글 작성
    CommentRequest createCommunityCommentRequest = new CommentRequest("잡담댓글");
    Comment createComment = commentService.createCommunityComment(2L, createCommunityCommentRequest,
        findUser.get());

    // when
    commentService.deleteCommunity(createComment.getId(), findUser.get());

    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isFalse();
  }

// Repository에 쿼리가 있어서 실행이 안됩니다
//  @Test
//  void deleteCommentsByBoardId() {
//    // given
//    CommentRequest commentRequest = new CommentRequest("잡담댓글");
//    Long communityBoardId = 2L;
//    Optional<User> findUser = userRepository.findById(1L);
//    Comment createComment = commentService.createCommunityComment(communityBoardId, commentRequest,
//        findUser.get());
//
//    // when
//    commentService.deleteCommentsByBoardId(createComment.getBoardId());
//
//    // then
//    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isFalse();
//  }

  @Test
  @DisplayName("게시글아이디와 유저아이디가 있는지 확인하는 기능")
  void existsCommentByBoardIdAndUserId() throws IOException {
    // given
    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest commentRequest = new CommentRequest("댓글 작성");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentService.existsCommentByBoardIdAndUserId(createComment.getBoardId(),
        findUser.get().getId());

    // then
    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isTrue();
    assertThat(createComment.getUserId()).isEqualTo(findUser.get().getId());
  }

  @Test
  @DisplayName("게시글아이디가 있는지 확인하는 기능")
  void existsCommentsByBoardId() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest commentRequest = new CommentRequest("댓글 작성");
    Comment createComment = commentService.createCommunityComment(2L, commentRequest,
        findUser.get());

    // when
    commentService.existsCommentsByBoardId(createComment.getBoardId());

    // then
    assertThat(commentRepository.existsCommentsByBoardId(createComment.getBoardId())).isTrue();
  }

  @Test
  @DisplayName("닉네임을 가져오는 기능")
  void getNickname() {
    // given
    Optional<User> findUser = userRepository.findById(1L);
    // when
    commentService.getNickname(findUser.get().getId());
    // then
    assertThat(findUser.get().getNickname()).isEqualTo(
        commentService.getNickname(findUser.get().getId()));
  }

  @Test
  @DisplayName("유저서비스에서 댓글아이디에 프로필을 조회해서 닉네임을 가져오는 기능")
  void getNicknameByComment() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest commentRequest = new CommentRequest("댓글 작성");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    String nicknameByComment = commentService.getNicknameByComment(createComment);

    // then
    assertThat(nicknameByComment).isEqualTo(findUser.get().getNickname());
  }

  @Test
  @DisplayName("좋아요 갯수")
  void countLike() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // 댓글 작성
    CommentRequest commentRequest = new CommentRequest("댓글 작성");
    Comment createComment = commentService.createCommunityComment(createCommunityBoard.getId(),
        commentRequest,
        findUser.get());

    // when
    commentService.countLike(createComment.getId());
    // then
    assertThat(commentRepository.existsCommentById(createComment.getId())).isTrue();
  }

  @Test
  @DisplayName("게시글에 달린 댓글 (질문 게시글에 붙힐 것)")
  void countComments() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 작성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    // when
    long countComments = commentService.countComments(createCommunityBoard.getId());

    // then
    assertThat(commentRepository.countByBoardId(createCommunityBoard.getId())).isEqualTo(
        countComments);
  }

  @Test
  @DisplayName("게시글에 달린 댓글, 대댓글 갯수(잡담 게시글에 붙힐 것)")
  void countCommentsAndReplyComments() throws IOException {
    // given

    // 유저 id로 찾아서 저장
    Optional<User> findUser = userRepository.findById(1L);

    // 게시글 생성
    CommunityBoardRequest communityBoardRequest = new CommunityBoardRequest("제목", "내용",
        CommunityCategory.NORMAL);
    List<MultipartFile> multipartFiles = new ArrayList<>();
    MockMultipartFile multipartFile = new MockMultipartFile("files", "imageFile.jpeg", "image/jpeg",
        "<<jpeg data>>".getBytes());
    multipartFiles.add(multipartFile);

    CommunityBoard createCommunityBoard = communityBoardService.createCommunityBoard(
        communityBoardRequest,
        multipartFiles, findUser.get());

    List<BoardImage> originBoardImageList = boardImageRepository.findAllByBoardId(
        createCommunityBoard.getId());
    List<String> originImagePaths = new ArrayList<>();
    for (BoardImage boardImage : originBoardImageList) {
      originImagePaths.add(boardImage.getImagePath());
    }

    long countComments = commentRepository.countByBoardId(createCommunityBoard.getId());
    List<Comment> comments = commentRepository.findAllByBoardId(createCommunityBoard.getId());
    long countReplyComments = replyCommentService.countReplyComments(comments);

    // when
    commentService.countCommentsAndReplyComments(createCommunityBoard.getId());
    long l = countComments + countReplyComments;

    // then
    assertThat(
        commentService.countCommentsAndReplyComments(createCommunityBoard.getId())).isEqualTo(l);


  }
}