package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.StatusSpecificDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;

    public List<Status> getStatuses(@PathVariable String boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ApiNotFoundException("Board id '" + boardId + "' not found");
        }
        return statusRepository.findByBoardId(boardId);
    }

    public StatusSpecificDto getStatusById(@PathVariable String boardId, @PathVariable Integer statusId) {
        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        return new StatusSpecificDto(
                status.getId(),
                new BoardDto(
                        status.getBoard().getId(),
                        status.getBoard().getName(),
                        status.getBoard().getVisibility(),
                        new UserDto(
                                status.getBoard().getUser().getOid(),
                                status.getBoard().getUser().getUsername()
                        )
                ),
                status.getName(),
                status.getDescription(),
                status.getCreated_on(),
                status.getUpdated_on()
        );
    }

    @Transactional
    public Status addStatus(@PathVariable String boardId, @RequestBody Status status) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        status.setBoard(existingBoard);
        status.setName(status.getName().trim()); // ตัดช่องว่างด้านหน้าและหลังชื่อ
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(@PathVariable String boardId,
                             @PathVariable Integer statusId,
                             @RequestBody Status status) {
        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' in board id '" + boardId + "' not found"));

        if (existingStatus.getName().contains("No Status") || existingStatus.getName().contains("Done")) {
            throw new ApiBadRequestException("Status name '" + existingStatus.getName() + "' cannot be deleted");
        }

        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        status.setName(status.getName().trim()); // ตัดช่องว่างด้านหน้าและหลังชื่อ
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(@PathVariable String boardId, @PathVariable Integer statusId) {
        Status statusToDelete = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        statusRepository.delete(statusToDelete);
    }

    @Transactional
    public void transferStatus(@PathVariable String boardId,
                               @PathVariable Integer statusId,
                               @PathVariable Integer newStatusId) {
        if (statusId.equals(newStatusId)) {
            throw new ApiBadRequestException("Status id '" + statusId + "' and new id '" + newStatusId + "' cannot be transferred");
        }

        Status deleteId = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        if (deleteId.getName().contains("No Status") || deleteId.getName().contains("Done")) {
            throw new ApiBadRequestException("Status name '" + deleteId.getName() + "' cannot be deleted");
        }

        Status transferId = statusRepository.findByBoardIdAndId(boardId, newStatusId)
                .orElseThrow(() -> new ApiNotFoundException("New status id '" + newStatusId + "' does not exist for board id '" + boardId + "'"));

        List<Task> tasks = taskRepository.findByStatus(deleteId);

        for (Task task : tasks) {
            task.setStatus(transferId);
        }

        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(deleteId);
    }
}
