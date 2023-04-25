package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.OrderMapper;
import com.raf.si.laboratoryservice.model.*;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.repository.*;
import com.raf.si.laboratoryservice.repository.filtering.filter.WorkOrderFilter;
import com.raf.si.laboratoryservice.repository.filtering.specification.WorkOrderSpecification;
import com.raf.si.laboratoryservice.service.WorkOrderService;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class WorkOrderServiceImplementation implements WorkOrderService {

    private final ReferralRepository referralRepository;
    private final LabAnalysisRepository labAnalysisRepository;
    private final LabWorkOrderRepository labWorkOrderRepository;
    private final AnalysisParameterResultRepository analysisParameterResultRepository;
    private final OrderMapper orderMapper;
    private final AnalysisParameterRepository analysisParameterRepository;

    public WorkOrderServiceImplementation(ReferralRepository referralRepository, LabAnalysisRepository labAnalysisRepository, LabWorkOrderRepository labWorkOrderRepository, AnalysisParameterResultRepository analysisParameterResultRepository, OrderMapper orderMapper, AnalysisParameterRepository analysisParameterRepository) {
        this.referralRepository = referralRepository;
        this.labAnalysisRepository = labAnalysisRepository;
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.analysisParameterResultRepository = analysisParameterResultRepository;
        this.orderMapper = orderMapper;
        this.analysisParameterRepository = analysisParameterRepository;
    }

    @Override
    public OrderResponse createOrder(Long orderId) {
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        UUID lbz = tokenPayload.getLbz();

        Referral referral = referralRepository.findById(orderId).orElseThrow(() -> {
            String errMessage = String.format("Uput sa id-om '%s' ne postoji", orderId);
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        });

        UUID lbp = referral.getLbp();
        String[] requiredAnalysis = referral.getRequiredAnalysis().split(",");
        List<String> requiredAnalysislist = Arrays.asList(requiredAnalysis);

        LabWorkOrder newOrder = new LabWorkOrder();
        newOrder.setLbp(lbp);
        newOrder.setLbzTechnician(lbz);
        newOrder.setCreationTime(new Date(System.currentTimeMillis()));
        newOrder.setReferral(referral);
        referral.setLabWorkOrder(newOrder);

        List<LabAnalysis> analysisList = labAnalysisRepository
                .findByNames(requiredAnalysislist)
                .orElse(new ArrayList<>());

        if((analysisList.size() == 0) || (analysisList.size() != requiredAnalysislist.size())){
                String errMessage = String.format("Nepoznate analize zahtevane");
                log.info(errMessage);
                throw new NotFoundException(errMessage);
        }

        List<AnalysisParameterResult> analysisParameterResults = new ArrayList<>();
        for(LabAnalysis analysis: analysisList){
            for (AnalysisParameter ap : analysis.getAnalysisParameters()){
                AnalysisParameterResult analysisParameterResult = new AnalysisParameterResult();
                analysisParameterResult.setLabWorkOrder(newOrder);
                analysisParameterResult.setAnalysisParameter(ap);
                analysisParameterResults.add(analysisParameterResult);
            }
        }

        labWorkOrderRepository.save(newOrder);
        referralRepository.save(referral);
        analysisParameterResultRepository.saveAll(analysisParameterResults);

        newOrder = labWorkOrderRepository.save(newOrder);
        return orderMapper.orderToOrderResponse(newOrder, analysisParameterResults);

    }

    @Override
    public OrderHistoryResponse orderHistory(OrderHistoryRequest historyRequest, Pageable pageable) {
        Page<LabWorkOrder> orders = labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatusIsNot(
                historyRequest.getLbp(), historyRequest.getStartDate(), historyRequest.getEndDate(),
                OrderStatus.NEOBRADJEN, pageable
        );
        return orderMapper.orderPageToOrderHistoryResponse(orders);
    }

    @Override
    public SaveResultResponse saveResult(SaveResultRequest saveRequest) {
        LabWorkOrder order = findOrder(saveRequest.getOrderId());
        if (order.getStatus().equals(OrderStatus.NEOBRADJEN)){
            order.setStatus(OrderStatus.U_OBRADI);
            order = labWorkOrderRepository.save(order);
        }

        AnalysisParameter analysisParameter =  analysisParameterRepository.findById(
                saveRequest.getParameterId()).orElseThrow(() -> {
                    String errMessage = String.format("Ne postoji parametar sa datim id-em: %s",
                            saveRequest.getOrderId());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
        });

        AnalysisParameterResult result = analysisParameterResultRepository
                .findAnalysisParameterResultByLabWorkOrderAndAnalysisParameter(
                        order, analysisParameter
                ).orElseThrow(() -> {
                    String errMessage = "Ne postoji rezultat sa date parametre.";
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        result.setResult(saveRequest.getResult());
        result.setDateAndTime(new Date(System.currentTimeMillis()));
        result.setLbzBiochemist(TokenPayloadUtil.getTokenPayload().getLbz());

        result = analysisParameterResultRepository.save(result);

        return orderMapper.resultToSaveResultResponse(result);
    }

    @Override
    public ResultResponse getResults(Long orderId) {
        List<String> permissions = TokenPayloadUtil.getTokenPayload().getPermissions();
        LabWorkOrder order = findOrder(orderId);
        if (!permissions.contains("ROLE_MED_BIOHEM") && !permissions.contains("ROLE_SPEC_MED_BIOHEM")) {
            if (!order.getStatus().equals(OrderStatus.OBRADJEN)) {
                String errMessage = String.format("Radni nalog sa id-om '%s' nije obradjen", orderId);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }
        return orderMapper.orderToResultResponse(order);
    }

    @Override
    public OrderHistoryResponse orderHistoryForLab(OrderHistoryForLabRequest request, Pageable pageable) {
        WorkOrderFilter filter = new WorkOrderFilter(
                request.getLbp(),
                request.getStartDate(),
                request.getEndDate(),
                OrderStatus.valueOfNotation(request.getOrderStatus())
        );
        WorkOrderSpecification spec = new WorkOrderSpecification(filter);

        Page<LabWorkOrder> orders = labWorkOrderRepository.findAll(spec, pageable);

        return orderMapper.orderPageToOrderHistoryForLabResponse(orders);
    }

    @Override
    public OrderResponse verify(Long orderId) {
        LabWorkOrder order = findOrder(orderId);

        for(AnalysisParameterResult apr : order.getAnalysisParameterResults()){
            if(apr.getResult() == null){
                String errMessage = "Nisu uneti svi rezultati analize parametara.";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }

        order.setStatus(OrderStatus.OBRADJEN);
        order.setLbzBiochemist(TokenPayloadUtil.getTokenPayload().getLbz());

        Referral referral = order.getReferral();
        referral.setStatus(ReferralStatus.REALIZOVAN);
        order.setReferral(referral);

        order = labWorkOrderRepository.save(order);

        return orderMapper.orderToOrderResponse(order);
    }

    private LabWorkOrder findOrder(Long orderId){
        return labWorkOrderRepository.findById(orderId)
                .orElseThrow(() -> {
                    String errMessage = String.format("Radni nalog sa id-om '%s' ne postoji", orderId);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }
}
