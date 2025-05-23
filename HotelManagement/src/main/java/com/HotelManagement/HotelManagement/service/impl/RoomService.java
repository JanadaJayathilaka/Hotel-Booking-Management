package com.HotelManagement.HotelManagement.service.impl;


import com.HotelManagement.HotelManagement.dto.Response;
import com.HotelManagement.HotelManagement.dto.RoomDTO;
import com.HotelManagement.HotelManagement.exception.OurException;
import com.HotelManagement.HotelManagement.model.Room;
import com.HotelManagement.HotelManagement.repository.BookingRepository;
import com.HotelManagement.HotelManagement.repository.RoomRepository;
import com.HotelManagement.HotelManagement.service.AwsS3Service;
import com.HotelManagement.HotelManagement.service.interfacE.IRoomService;
import com.HotelManagement.HotelManagement.utills.Utils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Response response = new Response();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomDescription(description);
            room.setRoomPrice(roomPrice);

            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public Response getAllRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);

        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get all rooms "+ e.getMessage());
        }

        return response;

    }

    @Override
    public Response deleteRoom(Long roomId) {
        Response response = new Response();

        try {
            roomRepository.findById(roomId).orElseThrow(()->new OurException("Room not found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Successful");


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error delete room "+ e.getMessage());
        }

        return response;
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Response response = new Response();

        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error update a room " + e.getMessage());
        }
        return response;
    }


    @Override
    public Response getRoomById(Long roomId) {
        Response response = new Response();

        try {
            Room room= roomRepository.findById(roomId).orElseThrow(()->new OurException("Room not found"));
            RoomDTO roomDTO= Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoom(roomDTO);


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get rooms by id"+ e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkoutDate, String roomType) {
        Response response = new Response();

        try {
            List<Room> availableRooms =roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate,checkoutDate,roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);



        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get all rooms by date and time"+ e.getMessage());
        }

        return response;
    }

    @Override
    public Response getAllAvailableRooms() {
        Response response = new Response();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setRoomList(roomDTOList);


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get all available rooms "+ e.getMessage());
        }

        return response;
    }
}
