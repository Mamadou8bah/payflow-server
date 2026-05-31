"use client";

import { useEffect, useMemo, useState } from "react";

type FaqSection = {
  title: string;
  description: string;
  questions: string[];
};

type FaqSectionProps = {
  sections: FaqSection[];
};

function slugify(value: string) {
  return `faq-${value.toLowerCase().replaceAll(" ", "-")}`;
}

export function FaqSection({ sections }: FaqSectionProps) {
  const sectionIds = useMemo(() => sections.map((section) => slugify(section.title)), [sections]);
  const [activeId, setActiveId] = useState(sectionIds[0] ?? "");

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        const visibleEntry = entries
          .filter((entry) => entry.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];

        if (visibleEntry?.target.id) {
          setActiveId(visibleEntry.target.id);
        }
      },
      {
        rootMargin: "-35% 0px -45% 0px",
        threshold: [0.15, 0.35, 0.55, 0.75],
      },
    );

    sectionIds.forEach((id) => {
      const element = document.getElementById(id);
      if (element) {
        observer.observe(element);
      }
    });

    return () => observer.disconnect();
  }, [sectionIds]);

  return (
    <section className="bg-orange-700 py-20 text-white md:py-24" id="faq">
      <div className="mx-auto grid w-[min(1280px,calc(100%-2rem))] gap-12 lg:grid-cols-[minmax(0,0.75fr)_minmax(480px,1.05fr)]">
        <div className="lg:sticky lg:top-28 lg:h-[calc(100vh-7rem)] lg:py-6">
          <p className="text-sm font-bold uppercase tracking-[0.22em] text-orange-300">FAQ</p>
          <h2 className="mt-4 text-4xl font-black leading-tight text-white md:text-5xl">Questions by what you need to do.</h2>
          <p className="mt-5 max-w-xl text-lg leading-relaxed text-white/80">
            Start with the user journey that matches you. The questions on the right move through each section as you scroll.
          </p>

          <div className="mt-8 flex flex-wrap gap-4 text-sm font-bold md:max-w-md">
            {sections.map((section) => {
              const id = slugify(section.title);
              const isActive = activeId === id;

              return (
                <a
                  key={section.title}
                  href={`#${id}`}
                  onClick={() => setActiveId(id)}
                  className={`rounded-full px-5 py-3 transition-colors ${
                    isActive ? "bg-white text-orange-700 shadow-sm" : "border border-white/70 text-white hover:bg-white/10"
                  }`}
                >
                  {section.title}
                </a>
              );
            })}
          </div>
        </div>

        <div className="space-y-10 lg:pb-[12vh]">
          {sections.map((section) => {
            const id = slugify(section.title);
            const isActive = activeId === id;

            return (
              <article key={section.title} id={id} className="flex min-h-[70vh] scroll-mt-28 items-start">
                <div
                  className={`w-full rounded-2xl p-6 text-white shadow-[0_35px_90px_rgba(15,23,42,0.18)] ring-1 transition-colors md:p-8 ${
                    isActive ? "bg-white/16 ring-white/25" : "bg-white/10 ring-white/10"
                  }`}
                >
                  <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-300">{section.title}</p>
                  <h3 className="mt-3 text-3xl font-black leading-tight text-white">{section.description}</h3>

                  <div className="mt-7 space-y-4">
                    {section.questions.map((question) => (
                      <div key={question} className="flex items-center justify-between gap-5 rounded-2xl bg-white/10 px-5 py-5 ring-1 ring-white/10">
                        <p className="text-lg font-bold leading-snug text-white">{question}</p>
                        <span className="text-3xl font-light leading-none text-orange-200">+</span>
                      </div>
                    ))}
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      </div>
    </section>
  );
}
