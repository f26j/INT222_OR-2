package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.services.BoardService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v3/boards")
@BeOr2CrossOrigin
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public String[] getBoards() {
        return new String[]{};
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardDto> getBoardById(@PathVariable String id) {
        List<Board> boards = boardService.getBoardById(id);
        return boards.stream().map(board -> new BoardDto(
                board.getId(),
                board.getName(),
                new UserDto(
                        board.getUser().getOid(),
                        board.getUser().getName()
                )
        )).toList();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BoardDto> addBoard(@RequestBody Board board) {
        BoardDto savedBoard = boardService.addBoard(board);
        return Collections.singletonList(modelMapper.map(savedBoard, BoardDto.class));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardDto> editBoard(@PathVariable String id, @RequestBody Board board) {
        BoardDto editedBoard = boardService.editBoard(id, board);
        return Collections.singletonList(modelMapper.map(editedBoard, BoardDto.class));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
    }
}
