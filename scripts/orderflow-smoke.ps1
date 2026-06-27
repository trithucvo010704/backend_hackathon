param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Email = "sale.admin@orderflow.local",
    [string]$Password = "password",
    [switch]$RunAi,
    [switch]$CreateOrder
)

$ErrorActionPreference = "Stop"

function Invoke-JsonApi {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers = @{},
        [object]$Body = $null
    )

    $arguments = @{
        Method = $Method
        Uri = $Uri
        Headers = $Headers
        ContentType = "application/json"
    }
    if ($null -ne $Body) {
        $arguments.Body = ($Body | ConvertTo-Json -Depth 20)
    }
    Invoke-RestMethod @arguments
}

Write-Host "OrderFlow smoke: login"
$login = Invoke-JsonApi -Method "POST" -Uri "$BaseUrl/api/auth/login" -Body @{
    email = $Email
    password = $Password
}

$token = $login.data.accessToken
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Login did not return accessToken"
}

$headers = @{ Authorization = "Bearer $token" }

Write-Host "OrderFlow smoke: /api/auth/me"
$me = Invoke-JsonApi -Method "GET" -Uri "$BaseUrl/api/auth/me" -Headers $headers
Write-Host ("Logged in as {0} ({1})" -f $me.data.email, $me.data.role)

Write-Host "OrderFlow smoke: master data"
$customersResponse = Invoke-JsonApi -Method "GET" -Uri "$BaseUrl/api/customers" -Headers $headers
$warehousesResponse = Invoke-JsonApi -Method "GET" -Uri "$BaseUrl/api/warehouses" -Headers $headers
$skusResponse = Invoke-JsonApi -Method "GET" -Uri "$BaseUrl/api/products/skus" -Headers $headers

$customers = @($customersResponse.data)
$warehouses = @($warehousesResponse.data)
$skus = @($skusResponse.data)
Write-Host ("Customers={0}; Warehouses={1}; SKUs={2}" -f $customers.Count, $warehouses.Count, $skus.Count)

if ($customers.Count -eq 0 -or $warehouses.Count -eq 0) {
    throw "Seed data is missing customers or warehouses"
}

if ($RunAi) {
    Write-Host "OrderFlow smoke: /api/ai/extract-order"
    $aiResponse = Invoke-JsonApi -Method "POST" -Uri "$BaseUrl/api/ai/extract-order" -Headers $headers -Body @{
        rawText = "Cong ty Minh Anh dat 10 cay ong Binh Minh phi 21 va 5 co 90 phi 27, giao cong trinh Quan 7 ngay 2026-06-29 gio hanh chinh."
    }
    Write-Host ("AI extracted lines={0}" -f @($aiResponse.data.lines).Count)
}

if ($CreateOrder) {
    Write-Host "OrderFlow smoke: /api/draft-orders/from-text"
    $customer = $customers[0]
    $projectsResponse = Invoke-JsonApi -Method "GET" -Uri "$BaseUrl/api/customers/$($customer.id)/projects" -Headers $headers
    $project = @($projectsResponse.data) | Select-Object -First 1
    $warehouse = $warehouses[0]

    $body = @{
        customerId = $customer.id
        projectId = if ($null -eq $project) { $null } else { $project.id }
        warehouseId = $warehouse.id
        rawText = "Cong ty Minh Anh dat 10 cay ong Binh Minh phi 21 va 5 co 90 phi 27, giao cong trinh Quan 7 ngay 2026-06-29 gio hanh chinh."
    }
    $orderResponse = Invoke-JsonApi -Method "POST" -Uri "$BaseUrl/api/draft-orders/from-text" -Headers $headers -Body $body
    Write-Host ("Draft order status={0}" -f $orderResponse.data.order.status)
}

Write-Host "OrderFlow smoke: PASS"
