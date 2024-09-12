package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.services.BoardService;

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
    public List<Board> getBoards() {
        return boardService.getBoards();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Board getBoardById(@PathVariable String id) {
        return boardService.getBoardById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDto addBoard(@RequestBody Board board) {
        return modelMapper.map(boardService.addBoard(board), BoardDto.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BoardDto editBoard(@PathVariable String id, @RequestBody Board board) {
        return modelMapper.map(boardService.editBoard(id, board), BoardDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
    }
}
