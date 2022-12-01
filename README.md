<img title="Volt Active Data" alt="Volt Active Data Logo" src="http://52.210.27.140:8090/voltdb-awswrangler-servlet/VoltActiveData.png">

# voltdb-aggdemo
A demonstration of how VoltDB can be used for the kind of streaming aggregation tasks common in Telco

# Overview
“Mediation” is the process of taking a firehouse of records generated by devices on a network and turning
it into a stream of validated and well organized records that are useful to downstream systems. Mediation
can also involve “Correlation”, which is joining two or more related streams of data that don’t have a 1:1
relationship with each other.
While legacy Mediation systems tended to revolve around the need to produce paper bills from telephone
switching equipment, the fundamental requirement to turn a chaotic stream of events into a manageable
stream of valid records is now relevant to a whole series of areas such as video game analytics, the IoT, and
more or less any scenario where events are being reported by devices ‘in the wild.
In theory processing this data sounds very simple and could easily be done with a simple RDBMS or even a
streaming SQL product.

But:

* THE VOLUMES ARE HUGE

It’s perfectly normal to see billions of records an hour, or more then 280K / second. A system also needs to
be able to process records at at least 1.5 times the speed they are created, so it can catch up after an outage.
The rules for turning the incoming stream into an outgoing stream are complicated.
In theory you can do mediation with a SQL ‘GROUP BY’ operation. In practice the rules for when to send a
record downstream are highly complicated and domain specific.

*  LATE RECORDS

A small proportion of records which show up will be either out of sequence or simply late. When possible
they need to be processed. This means that some sessions will last much longer than others.


* DUPLICATE RECORDS CAN’T BE TOLERATED

If we don’t spot duplicates, we run the risk of double counting usage and overcharging end users. This
sounds like a simple issue, but we’re expecting billions of records per day and need to keep them for days.
Note that overwriting a record with one that has the same key but different values could lead to values
changing ‘after the fact’ - once we’ve aggregated a record we can’t go back and do it again.

* MISSING RECORDS

In the real world, you will inevitably have some records that never make it to their destination. Your system
needs to cope with these rationally.

* INCORRECTLY TIMESTAMPED RECORDS

In real world systems we sometimes see records that are from devices that either have wildly inaccurate
clocks or have never set their system clock. Your system needs to prevent these from being processed.

* WE SOMETIMES NEED TO INSPECT ‘IN FLIGHT’ DATA

In many emerging Use Cases the data stream is looked at in two ways. While most aggregate data
influences events over a time period of hours or days, a subset of the data deals refers to ongoing events
that can be influenced over timescales ranging from milliseconds to seconds. This is challenging, as
traditionally we can do high volumes in minutes, or low volumes in milliseconds, but not high volumes
in milliseconds.

# WHAT THE MEDIATION APPLICATION DEMONSTRATES

Mediation has turned into a complex, multi-faceted problem. The issues we have to address are not just
relevant to Telco, but also to Video Game Analytics, stock trade reports and other streaming data sets.

* LOW LATENCY, HIGH VOLUME TRANSACTIONS

The mediation application can process hundreds of thousands of records per second, while still allowing
users to inspect individual sessions in real time.

* COMPLEX DECISIONS

Each mediation decision is a non-trivial event, and involves both sanity checking the incoming record as
well as making an individual, context aware decision on whether to generate an output record.

* TRANSACTIONAL CONSISTENCY

The mediation application uses Volt Active Data’s architecture to provide immediately consistent answers at
mass scale, without re-calculation afterwards or giving misleading answers due to ‘eventual consistency’.
We can guarantee that the numbers being sent downstream accurately reflect the data arriving.

* HIGH AVAILABILITY

The system will continue to run if a node is brought down. The node will rejoin without problems.

* SCALE

We will show that we can support 10’s of thousands of transactions per second on a 3 node generic cluster in AWS.

* CLOUD NATIVE

This sandbox is created using an AWS CloudFormation script. We could have done the whole thing in Kubernetes,
but that means that anyone using the sandbox needs to know Kubernetes. For simplicity, we’ve left it out.

* VISUALIZED RESULTS

The sandbox includes a Grafana dashboard that allows you see what’s going on from an Operating System,
Database and Business perspective.