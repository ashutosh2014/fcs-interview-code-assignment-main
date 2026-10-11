# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. 
The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. 
Think about what are important considerations for this, what are previous experiences 
that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

Challenges
 - Labor costs: the delivery guy deliverying across multiple warehouse/stores, this makes salary and accounting difficult
 - Delivery costs: One-delivery can make delivery to diff warehouse, cost to be divided fairly
 - Direct cost: Rent, Admin Exp, Utilites like electricty, 
 - Inventory Cost: Holding Product i.e. inventory cost
 - Historical Data: when warehouse replaced, the Business Unit mapped to new one, may have old warehouse historical data associated

Points to consider
 - Direct Cost to align with warehouse/store
 - Cost evaluation should be allocated using correct delivery cost, delivery guy hours rate, weight of delivery, no of delevieres. Each Cost should have amount, cost type, date, and the relevant Warehouse or Store identifier.
 - Historical cost records should reference to the specific entity instance, not just its Business Unit Code.

Related experience — Arumstreet(jewellery e-commerce): I built a platform where customers browse a store listing, shop by store section,
and discover products through Instagram-style reels, while merchants run everything from an admin with analytics and offers tied to specific stores. 
That surface looks like marketing and UX, but underneath it is the same allocation problem in miniature: every order, reel view, 
and promotion needs to roll up to which store generated revenue versus cost.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
[ fill here your answer ]

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
[ fill here your answer ]

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
[ fill here your answer ]

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
[ fill here your answer ]

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
