package sit.int221.BEOR2.db1.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<Board> getBoardById(@PathVariable String id) {
        return Collections.singletonList(boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found")));
    }

    @Transactional
    public BoardDto addBoard(@RequestBody Board board) {
        if (board.getUser() == null) {
            throw new ApiBadRequestException("User is required to create a board");
        }

        if (boardRepository.existsById(board.getId())) {
            throw new ApiBadRequestException("Board id '" + board.getId() + "' already exists");
        }

        String boardName = board.getName();
        if (boardName == null || boardName.trim().isEmpty() || boardName.length() > 120) {
            throw new ApiBadRequestException("Board name cannot be empty or overlength");
        }

        modelMapper.typeMap(Board.class, BoardDto.class).addMappings(mapper -> {
            mapper.map(Board::getUser, BoardDto::setOwner);
        });

        Board savedBoard = boardRepository.saveAndFlush(board);
        return modelMapper.map(savedBoard, BoardDto.class);
    }

    @Transactional
    public BoardDto editBoard(@PathVariable String id, @RequestBody Board board) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        modelMapper.typeMap(Board.class, BoardDto.class).addMappings(mapper -> {
            mapper.map(Board::getUser, BoardDto::setOwner);
        });

        board.setId(existingBoard.getId());
        board.setUser(existingBoard.getUser());
        Board editedBoard = boardRepository.saveAndFlush(board);
        return modelMapper.map(editedBoard, BoardDto.class);
    }

    @Transactional
    public void deleteBoard(@PathVariable String id) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        boardRepository.deleteById(id);
    }
}
