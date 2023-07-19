package com.app.comentarioserver.service;

import com.app.comentarioserver.dto.BoardDto;
import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.imagekit_upload.ImageUpload;
import com.app.comentarioserver.repository.BoardRepository;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.FileCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final ImageUpload imageUpload;
    private final UserService userService;

    public List<Board> allBoards() {
        return boardRepository.findAll();
    }

    public Board addBoard(Board board, MultipartFile file) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        board.setImageData(imageUpload.uploadImage(file, "Board-cover/"));
        Board newBoard = boardRepository.save(board);
        userService.addBoardToTheUser(newBoard, board.getUsername());
        return newBoard;
    }
    public Board updateBoard(BoardDto boardDto, String boardId, MultipartFile file) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        Board board = getBoard(boardId);
        board.setTitle(boardDto.title());
        board.setDescription(boardDto.description());
        imageUpload.deleteImage(board.getImageData().getImageId());
        board.setImageData(imageUpload.uploadImage(file, "Board-cover/"));
        board.setSelf(boardDto.isSelf());
        board.setUrl(boardDto.url());
        return boardRepository.save(board);
    }

    public Board updateBoard(BoardDto boardDto, String boardId) {
        Board board = getBoard(boardId);
        board.setTitle(boardDto.title());
        board.setDescription(boardDto.description());
        board.setSelf(boardDto.isSelf());
        board.setUrl(boardDto.url());
        return boardRepository.save(board);
    }

    public Board updateClickCount(String boardId) {
        Board board = getBoard(boardId);
        board.setUrlClick();
        return boardRepository.save(board);
    }

    public Board getBoard(String boardId) {
        return boardRepository.findById(boardId).orElseThrow();
    }

    public void deleteAll() {
        boardRepository.deleteAll();
    }


    public void addFeedbackToTheBoard(String id, Feedback feedback) {
        Board board = boardRepository.findById(id).orElseThrow();
        board.setFeedbacks(feedback);
        boardRepository.save(board);
    }

}
