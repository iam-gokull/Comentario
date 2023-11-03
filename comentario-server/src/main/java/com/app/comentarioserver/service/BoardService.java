package com.app.comentarioserver.service;

import com.app.comentarioserver.dto.BoardDto;
import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.exception.BoardNotFoundException;
import com.app.comentarioserver.exception.URLAlreadyExistsException;
import com.app.comentarioserver.exception.URLNotValidException;
import com.app.comentarioserver.exception.UnableToAddFeedbackException;
import com.app.comentarioserver.imagekit_upload.ImageUpload;
import com.app.comentarioserver.repository.BoardRepository;
import com.app.comentarioserver.repository.FeedbackRepository;
import io.imagekit.sdk.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final FeedbackRepository feedbackRepository;

    private final ImageUpload imageUpload;
    private final UserService userService;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board addBoard(Board board, MultipartFile file) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        if (checkURL(board.getUrl())) {
            board.setImageData(imageUpload.uploadImage(file, "Board-cover/"));
            Board newBoard = boardRepository.save(board);
            userService.addBoardToTheUser(newBoard, board.getUsername());
            return newBoard;
        }

        return board;
    }
    public Board updateBoard(BoardDto boardDto, String boardId, MultipartFile file) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        Board board = getBoardById(boardId);
        board.setUrl("");
        boardRepository.save(board);
        if (checkURL(boardDto.url())) {
            board.setTitle(boardDto.title());
            board.setDescription(boardDto.description());
            if (board.getImageData().getImageUrl().contains("imagekit")) {
                imageUpload.deleteImage(board.getImageData().getImageId());
            }
            board.setImageData(imageUpload.uploadImage(file, "Board-cover/"));
            board.setSelf(boardDto.isSelf());
            board.setUrl(boardDto.url());
            return boardRepository.save(board);
        }

        return board;
    }

    public Board updateBoard(BoardDto boardDto, String boardId) {
        Board board = getBoardById(boardId);
        board.setUrl("");
        boardRepository.save(board);
        if (checkURL(boardDto.url())) {
            board.setTitle(boardDto.title());
            board.setDescription(boardDto.description());
            board.setSelf(boardDto.isSelf());
            board.setUrl(boardDto.url());
            return boardRepository.save(board);
        }
        return board;
    }

    public boolean checkURL(String url) {
        if (!boardRepository.existsByUrl(url)) {
            return validateURL(url);
        } else {
            throw new URLAlreadyExistsException("This URL already exists");
        }
    }

    public boolean validateURL(String verifyUrl) {
        try {
            URL url = new URL(verifyUrl);
            String protocol = url.getProtocol();

            if (!protocol.equalsIgnoreCase("https")) {
                return false;
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");

            return connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;

        } catch (IOException e) {
            throw new URLNotValidException("URL is not valid");
        }
    }

    public Board updateClickCount(String boardId) {
        Board board = getBoardById(boardId);
        board.setUrlClick();
        return boardRepository.save(board);
    }

    public Board getBoardById(String boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("Unable to find board with id " + boardId));
    }

    public boolean addFeedbackToTheBoard(String id, Feedback feedback) {
        Board board = getBoardById(id);
        board.setFeedbacks(feedback);

        Board updatedBoard = boardRepository.save(board);
        if (!updatedBoard.getFeedbacks().contains(feedback)) {
            feedbackRepository.deleteById(feedback.getId());
            throw new UnableToAddFeedbackException("Unable to add feedback");
        } else {
            return true;
        }

    }

}
