package com.app.comentarioserver.service;

import com.app.comentarioserver.dto.BoardDto;
import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.exception.BoardNotFoundException;
import com.app.comentarioserver.exception.URLAlreadyExistsException;
import com.app.comentarioserver.imagekit_upload.ImageUpload;
import com.app.comentarioserver.pojo.ImageData;
import com.app.comentarioserver.repository.BoardRepository;
import io.imagekit.sdk.exceptions.*;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;


    @Mock
    private ImageUpload imageUpload;

    @Mock
    private UserService userService;

    @Test
    void test_gat_all_boards() {
        Board board1 = new Board();
        board1.setId("1");
        board1.setTitle("Test board 1");

        Board board2 = new Board();
        board2.setId("1");
        board2.setTitle("Test board 2");

        Board board3 = new Board();
        board3.setId("1");
        board3.setTitle("Test board 3");

        List<Board> boards = List.of(board1, board2, board3);
        Mockito.when(boardRepository.findAll()).thenReturn(boards);

        List<Board> result = boardService.getAllBoards();
        assertThat(result).hasSameSizeAs(boards);
        assertThat(result.get(0).getTitle()).isEqualTo(board1.getTitle());
    }

    @Test
    void test_add_board_for_new_board() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        Board board = new Board();
        board.setId("1");
        board.setTitle("Test board 1");
        board.setUrl("https://www.example.com/");

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG.toString(),
                new byte[1024]);

        ImageData imageData = new ImageData("https://www.example.com/", "id");

        Mockito.when(imageUpload.uploadImage(Mockito.any(MultipartFile.class), Mockito.anyString())).thenReturn(imageData);
        Mockito.when((boardRepository.save(board))).thenReturn(board);
        Mockito.doNothing().when(userService).addBoardToTheUser(board, board.getUsername());

        Board result = boardService.addBoard(board, file);
        assertThat(result).isNotNull();
    }

    @Test
    void test_add_board_for_existing_board() {
        Board board = new Board();
        board.setId("1");
        board.setTitle("Test board 1");
        board.setUrl("https://www.example.com/");

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG.toString(),
                new byte[1024]);

        Mockito.when(boardRepository.existsByUrl(board.getUrl())).thenReturn(true);

        Exception ex = assertThrows(URLAlreadyExistsException.class, () -> boardService.addBoard(board, file));
        assertThat(ex.getMessage()).isEqualTo("This URL already exists");

    }

    @Test
    void test_update_Board_with_image() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        Board board = new Board();
        board.setId("1");
        board.setTitle("Test board");
        board.setUrl("https://www.exa.com/");
        board.setDescription("Test description");

        ImageData imageData = new ImageData("https://www.example.com/", "id");
        board.setImageData(imageData);

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG.toString(),
                new byte[1024]);

        Mockito.when(boardRepository.findById(Mockito.anyString())).thenReturn(Optional.of(board));
        Mockito.when((boardRepository.save(Mockito.any(Board.class)))).thenReturn(board);

        BoardDto boardDto = new BoardDto("title", "description", "https://www.example.com/", true, "test User");

        Board result = boardService.updateBoard(boardDto, "1", file);
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getUrl()).isEqualTo("https://www.example.com/");
        assertThat(result.getDescription()).isEqualTo("description");

    }

    @Test
    void test_update_Board_without_image() {
        Board board = new Board();
        board.setId("1");
        board.setTitle("Test board");
        board.setUrl("https://www.exa.com/");
        board.setDescription("Test description");

        ImageData imageData = new ImageData("https://www.example.com/", "id");
        board.setImageData(imageData);

        Mockito.when(boardRepository.findById(Mockito.anyString())).thenReturn(Optional.of(board));
        Mockito.when((boardRepository.save(Mockito.any(Board.class)))).thenReturn(board);

        BoardDto boardDto = new BoardDto("title", "description", "https://www.example.com/", true, "test User");

        Board result = boardService.updateBoard(boardDto, "1");
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getUrl()).isEqualTo("https://www.example.com/");
        assertThat(result.getDescription()).isEqualTo("description");

    }

    @Test
    void test_check_URL_for_new_URL() {
        Mockito.when(boardRepository.existsByUrl(Mockito.anyString())).thenReturn(false);
        assertThat(boardService.checkURL("https://www.example.com")).isTrue();
    }

    @Test
    void test_check_URL_for_existing_URL() {
        Mockito.when(boardRepository.existsByUrl(Mockito.anyString())).thenReturn(true);
        Exception ex = assertThrows(URLAlreadyExistsException.class, () -> boardService.checkURL("https://www.example.com"));
        assertThat(ex.getMessage()).isEqualTo("This URL already exists");
    }

    @Test
    void test_validate_valid_URL() {
        boolean result = boardService.validateURL("https://www.example.com");
        assertThat(result).isTrue();
    }

    @Test
    void test_validate_invalid_URL() {
        boolean result = boardService.validateURL("http://www.example.com");
        assertThat(result).isFalse();
    }

    @Test
    void test_update_click_count() {
        Board board = new Board();
        board.setId("id");
        board.setTitle("Test board");
        board.setUrlClickCount(5);

        Board updatedBoard = new Board();
        updatedBoard.setId("id");
        updatedBoard.setTitle("Test board");
        updatedBoard.setUrlClickCount(6);

        Mockito.when(boardRepository.findById("id")).thenReturn(Optional.of(board));
        Mockito.when(boardRepository.save(Mockito.any(Board.class))).thenReturn(updatedBoard);

        Board result = boardService.updateClickCount("id");
        assertThat(result.getUrlClickCount()).isEqualTo(6);
    }

    @Test
    void test_get_board_by_valid_id() {
        Board board = new Board();
        board.setId("id");
        board.setTitle("Test board");

        Mockito.when(boardRepository.findById(Mockito.anyString())).thenReturn(Optional.of(board));
        Board result = boardService.getBoardById("id");

        assertThat(result.getTitle()).isEqualTo("Test board");
    }

    @Test
    void test_get_board_by_invalid_id() {
        Board board = new Board();
        board.setId("id");
        board.setTitle("Test board");
        String boardId = board.getId();
        Exception ex = assertThrows(BoardNotFoundException.class, () -> boardService.getBoardById(boardId));
        assertThat(ex.getMessage()).isEqualTo("Unable to find board with id " + boardId);
    }


    @Test
    void test_add_feedback_to_the_board() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Test feedback");
        feedback.setBoardId("1");
        feedback.setUsername("User");

        Board board = new Board();
        board.setId("id");
        board.setTitle("Test board");

        Mockito.when(boardRepository.findById(Mockito.anyString())).thenReturn(Optional.of(board));
        Mockito.when(boardRepository.save(Mockito.any(Board.class))).thenReturn(board);
        boolean result = boardService.addFeedbackToTheBoard("id", feedback);
        assertThat(result).isTrue();
    }
}