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
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. 
The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. 
How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

I will start by checking the current order network and where the company is spending the money relative to the value it delivers.
 - Network design: Determine whether the number and locations of Warehouses are appropriate for demand, geographic coverage, and capacity requirements.
 - Utilization: Identify underutilized Warehouses, excess inventory, inefficient storage, and unnecessary Store–Warehouse associations.
 - Operational processes: Identify opportunities to improve deliverying, picking, devlivery guy  allocation, and Warehouse replacement decisions.

How I would identify, prioritize, and implement:
 - Baseline: Current cost by warehouse, store, and location using the same allocation model. 
 - Opportunity sizing: Pareto analysis—which sites or lanes drive 80% of cost or variance? 
 - Prioritize with impact × feasibility × risk to service; quick wins (utilization, slotting) before structural changes (closing or replacing warehouses). 
 - Pilot on one location or business unit; measure cost and KPIs for a full cycle (e.g. a festival season). 
 - Scale with playbooks; update master data (warehouse replace, fulfillment links) and budgets when the network changes.

Questions:

- What service levels are non-negotiable (same-day to store, max warehouses per store/product)?
- Are optimization goals global or per country/location?
- Who approves structural changes (replace warehouse, change fulfillment associations)?
- Are we optimizing for total network cost, individual Warehouse profitability, or cost per unit delivered?


Considerations: Optimization without accurate tracking (Scenario 1) optimizes the wrong thing. 
Constraints in this codebase—max warehouses per location, capacity vs stock, fulfillment limits—are proxies for service and risk;
cost initiatives must respect them or explicitly ask to change the business rules.

From Arumstreet: we used store-level analytics (which reels and offers moved which SKUs) to stop funding low-ROI campaigns. 
The same mindset applies here—prioritize cost initiatives where store–warehouse fulfillment links and volume data show clear waste 
(e.g. serving a store from too many warehouses, or offers that drive high-touch picks without margin).

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
