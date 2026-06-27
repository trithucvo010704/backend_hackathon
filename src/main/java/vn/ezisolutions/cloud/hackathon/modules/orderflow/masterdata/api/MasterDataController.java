package vn.ezisolutions.cloud.hackathon.modules.orderflow.masterdata.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.CustomerCreditProfileRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.CustomerProjectRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.CustomerRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.InventoryBalanceRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.PriceListRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProductAliasRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProductSkuRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.WarehouseRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.security.OrderFlowSecurity;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MasterDataController {
    private final CustomerRepository customerRepository;
    private final CustomerProjectRepository customerProjectRepository;
    private final ProductSkuRepository productSkuRepository;
    private final ProductAliasRepository productAliasRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryBalanceRepository inventoryBalanceRepository;
    private final PriceListRepository priceListRepository;
    private final CustomerCreditProfileRepository customerCreditProfileRepository;

    @GetMapping("/customers")
    public BaseResponse customers() {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(customerRepository.findByOrganizationIdOrderByName(organizationId));
    }

    @GetMapping("/customers/{id}")
    public BaseResponse customer(@PathVariable UUID id) {
        return BaseResponse.success(customerRepository.findById(id).orElseThrow());
    }

    @GetMapping("/customers/{id}/projects")
    public BaseResponse customerProjects(@PathVariable UUID id) {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(customerProjectRepository.findByOrganizationIdAndCustomerIdAndActiveTrueOrderByName(organizationId, id));
    }

    @GetMapping("/products/skus")
    public BaseResponse productSkus() {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(productSkuRepository.findByOrganizationIdAndActiveTrueOrderBySkuCode(organizationId));
    }

    @GetMapping("/products/skus/{id}")
    public BaseResponse productSku(@PathVariable UUID id) {
        return BaseResponse.success(productSkuRepository.findById(id).orElseThrow());
    }

    @GetMapping("/products/aliases")
    public BaseResponse productAliases() {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(productAliasRepository.findByOrganizationIdAndActiveTrue(organizationId));
    }

    @GetMapping("/warehouses")
    public BaseResponse warehouses() {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(warehouseRepository.findByOrganizationIdAndActiveTrueOrderByName(organizationId));
    }

    @GetMapping("/inventory/balances")
    public BaseResponse inventoryBalances(@RequestParam(required = false) UUID warehouseId) {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(inventoryBalanceRepository.findByOrganizationId(organizationId).stream()
                .filter(balance -> warehouseId == null || warehouseId.equals(balance.getWarehouseId()))
                .toList());
    }

    @GetMapping("/price-lists")
    public BaseResponse priceLists() {
        UUID organizationId = OrderFlowSecurity.currentUser().organizationId();
        return BaseResponse.success(priceListRepository.findByOrganizationIdAndActiveTrueOrderByPriorityAsc(organizationId));
    }

    @GetMapping("/credit-profiles/{customerId}")
    public BaseResponse creditProfile(@PathVariable UUID customerId) {
        return BaseResponse.success(customerCreditProfileRepository.findById(customerId).orElseThrow());
    }
}
