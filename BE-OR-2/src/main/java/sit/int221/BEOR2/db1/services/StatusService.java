package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.dtos.StatusSpecificDto;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
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
    @Autowired
    private UserRepository userRepository;

    // ดึงสถานะทั้งหมดของ Board ที่ระบุ
    public List<Status> getStatuses(String boardId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        if (user == null && board.getVisibility().equals("PRIVATE")){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }

        return statusRepository.findByBoardId(boardId);
    }

    // ดึงสถานะเฉพาะเจาะจงโดยใช้ boardId และ statusId
    public StatusSpecificDto getStatusById(String boardId, Integer statusId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        if (user == null && board.getVisibility().equals("PRIVATE")){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }

        // Step 2: ตรวจสอบว่า Status มีอยู่ใน board ที่ระบุหรือไม่ (404)
        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        // Step 3: การตอบกลับปกติ (200)
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

    // เพิ่ม Status ใหม่ใน Board ที่กำหนด
    @Transactional
    public Status addStatus(String boardId, Status status) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }
        if (status == null){
            throw new ApiBadRequestException("Nobody Know");
        }

        // Step 2: ตรวจสอบว่า Status ซ้ำหรือไม่ (400 Bad Request)
        if (statusRepository.existsByNameAndBoardId(status.getName().trim(), boardId)) {
            throw new ApiBadRequestException("Status name already exists on this board.");
        }


        status.setBoard(existingBoard);
        status.setName(status.getName().trim());  // Trim name for consistency
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(String boardId, Integer statusId, Status status) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        boolean isOwner = board.getUser().getOid().equals(user.getOid());
        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }

        if (status == null){
            throw new ApiBadRequestException("Nobody Know");
        }

        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' in board id '" + boardId + "' not found"));

//        if (existingStatus.getName().equalsIgnoreCase("No Status") || existingStatus.getName().equalsIgnoreCase("Done")) {
//            throw new ApiBadRequestException("The status '" + existingStatus.getName() + "' cannot be modified.");
//        }


        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        status.setName(status.getName().trim());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(String boardId, Integer statusId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        boolean isOwner = board.getUser().getOid().equals(user.getOid());
        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }
        Status statusToDelete = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        if (statusToDelete.getName().equalsIgnoreCase("No Status") || statusToDelete.getName().equalsIgnoreCase("Done")) {
            throw new ApiBadRequestException("The status '" + statusToDelete.getName() + "' cannot be deleted.");
        }

        statusRepository.delete(statusToDelete);
    }

    @Transactional
    public void transferStatus(String boardId, Integer statusId, Integer newStatusId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        boolean isOwner = board.getUser().getOid().equals(user.getOid());
        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner){
            throw new ApiForbiddenException("Acess Denied");
        }else {
            checkBoardAccess(boardId);
        }

        if (statusId.equals(newStatusId)) {
            throw new ApiBadRequestException("Cannot transfer tasks to the same status.");
        }

        Status oldStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Old status id '" + statusId + "' does not exist for board id '" + boardId + "'"));

        Status newStatus = statusRepository.findByBoardIdAndId(boardId, newStatusId)
                .orElseThrow(() -> new ApiNotFoundException("New status id '" + newStatusId + "' does not exist for board id '" + boardId + "'"));

        List<Task> tasks = taskRepository.findByStatus(oldStatus);
        tasks.forEach(task -> task.setStatus(newStatus));
        taskRepository.saveAllAndFlush(tasks);

        statusRepository.delete(oldStatus);
    }

    private void checkBoardAccess(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userName + "Status");
        User user = userRepository.findByUsername(userName)
                .orElse(null);
        if (user != null) {
            boolean isOwner = board.getUser().getOid().equals(user.getOid());
            boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());

//         ถ้าไม่ใช่เจ้าของและบอร์ดเป็น private ให้ส่ง 403 Forbidden
            if (!isOwner && !isPublic) {
                throw new ApiForbiddenException("Access denied: You do not have permission to access this board.");
            }
        }
    }
}
