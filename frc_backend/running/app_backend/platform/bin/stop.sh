#!/bin/sh
cd ..
cd ..

if [ -f "DWApiGatewayApplication.pid" ]; then
    cat DWApiGatewayApplication.pid | xargs -i kill -9 {}
fi

echo DWApiGatewayApplication is Stop.
