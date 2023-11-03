package com.app.comentarioserver.controller;

import com.app.comentarioserver.dto.BoardDto;
import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.imagekit.sdk.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization")
@RequiredArgsConstructor
@RequestMapping(path = "/boards", produces = "application/json")
public class BoardController {

    private final BoardService boardService;

    private final ObjectMapper objectMapper;



    @GetMapping("/all-boards")
    public ResponseEntity<List<Board>> allBoards() {
        return new ResponseEntity<>(boardService.getAllBoards(), HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Board> addBoard(@RequestParam("file") MultipartFile file, @RequestParam("data") String board) throws IOException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        BoardDto boardDto = objectMapper.readValue(board, BoardDto.class);
        Board newBoard = new Board(boardDto);
        return new ResponseEntity<>(boardService.addBoard(newBoard, file), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{boardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Board> updateBoard(@PathVariable String boardId, @RequestParam("file") MultipartFile file, @RequestParam("data") String board) throws IOException, ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        BoardDto boardDto = objectMapper.readValue(board, BoardDto.class);
        return new ResponseEntity<>(boardService.updateBoard(boardDto, boardId, file), HttpStatus.OK);
    }

    @PutMapping(value = "/update/data/{boardId}")
    public ResponseEntity<Board> updateBoard(@PathVariable String boardId, @RequestBody BoardDto boardDto) {
        return new ResponseEntity<>(boardService.updateBoard(boardDto, boardId), HttpStatus.OK);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<Board> getBoard(@PathVariable String boardId) {
        return new ResponseEntity<>(boardService.getBoardById(boardId), HttpStatus.OK);
    }

    @PutMapping(value = "/update/url-click/{boardId}")
    public ResponseEntity<Board> urlClicked(@PathVariable String boardId) {
        return new ResponseEntity<>(boardService.updateClickCount(boardId), HttpStatus.OK);
    }

}
