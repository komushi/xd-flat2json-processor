# README #

### What is this repository for? ###

# How do I get set up? #

* SpringXD 1.3.1
* Maven

# How to run the simulator #
Simulator is a program to read from .csv file and then post to HTTP.


# Run ReadSampleFile with TaxiTransactionConverter #

## Step 1. Copy library ./lib/openmap-5.1.13-1.jar to $XD_HOME/lib


## Step 2. And register the module in xd-shell:

```
module upload --file ./target/FlatToJsonConverter-0.1.0.jar --name flat2json --type processor
```


## Step 3. Create and deploy a stream to test in xd-shell:

```
stream create test --definition "http --port=9090 | flat2json --inputType=text/plain --outputType=application/json |log" --deploy
```

## Step 4. And run the program using in xd-shell:

C161.160_C180.155
-----------------
```
http post --target http://localhost:9090  --contentType "application/text" --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.855383,40.779728,-73.967758,40.760326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"
```

C161.137_C163.155
-----------------
```
http post --target http://localhost:9090  --contentType "application/text" --data "468244D1361B8A3EB8D206CC394BC9E9,BB899DFEA9CC964B50C540A1D685CCFB,2013-01-01 00:00:00,2013-01-01 00:04:00,240,1.71,-73.955383,40.779728,-73.967758,40.860326,CSH,6.50,0.50,0.50,0.00,0.00,7.50"
```


## Step 5. Then you will be able to see output log:

```
2016-06-16T23:30:41+0900 1.3.1.RELEASE INFO pool-35-thread-4 sink.test - {"uuid":"7a49cba6-d3fc-47a4-8e1a-c2503b8144e8","route":"C161.137_C163.155","timestamp":1466087441276,"pickupLatitude":40.860326,"pickupLongitude":-73.967758,"dropoffLatitude":40.779728,"dropoffLongitude":-73.955383,"pickupDatetime":"2013-01-01 00:00:00","dropoffDatetime":"2013-01-01 00:04:00"}
```

## Step 6. Set up a gemfire sink stream

```
stream create RegionRaw --definition "http --port=9090 | flat2json --inputType=text/plain --outputType=application/json |gemfire-json-server --regionName=RegionRaw --host=localhost --port=10334 --useLocator=true --keyExpression=payload.getField('uuid')" --deploy
```

## Step 7. Check the region data in gfsh

```
gfsh>query --query="select * from /RegionRaw"


Result     : true
startCount : 0
endCount   : 20
Rows       : 1

                uuid                 |       route       |   timestamp   | pickupLatitude | pickupLongitude | dropoffLatitude | dropoffLongitude |   pickupDatetime    | dropoffDatetime
------------------------------------ | ----------------- | ------------- | -------------- | --------------- | --------------- | ---------------- | ------------------- | -------------------
48f553e2-cd5d-4617-897d-af7eddc2e282 | C161.137_C163.155 | 1466087730167 | 40.860326      | -73.967758      | 40.779728       | -73.955383       | 2013-01-01 00:00:00 | 2013-01-01 00:04:00

NEXT_STEP_NAME : END
```