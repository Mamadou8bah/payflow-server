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
    <section className="bg-orange-600 py-14 text-slate-900 sm:py-20 md:py-24" id="faq">
      <div className="mx-auto grid w-[min(1280px,calc(100%-1.5rem))] gap-10 lg:grid-cols-[minmax(0,0.75fr)_minmax(0,1.05fr)] lg:gap-12">
        <div className="lg:sticky lg:top-28 lg:h-[calc(100vh-7rem)] lg:py-6">
          <p className="text-sm font-bold uppercase tracking-[0.22em] text-orange-900">FAQ</p>
          <h2 className="mt-4 text-3xl font-black leading-tight text-slate-950 sm:text-4xl md:text-5xl">Questions by what you need to do.</h2>
          <p className="mt-5 max-w-xl text-base leading-relaxed text-slate-800 sm:text-lg">
            Start with the user journey that matches you. The questions on the right move through each section as you scroll.
          </p>

          <div className="mt-8 flex flex-wrap gap-3 text-sm font-bold md:max-w-md md:gap-4">
            {sections.map((section) => {
              const id = slugify(section.title);
              const isActive = activeId === id;

              return (
                <a
                  key={section.title}
                  href={`#${id}`}
                  onClick={() => setActiveId(id)}
                  className={`rounded-full px-4 py-2.5 transition-colors sm:px-5 sm:py-3 ${
                    isActive
                      ? "bg-slate-950 text-white"
                      : "border border-slate-900/30 text-slate-900 hover:bg-slate-950 hover:text-white"
                  }`}
                >
                  {section.title}
                </a>
              );
            })}
          </div>
        </div>

        <div className="space-y-6 sm:space-y-10 lg:pb-[12vh]">
          {sections.map((section) => {
            const id = slugify(section.title);
            const isActive = activeId === id;

            return (
              <article key={section.title} id={id} className="flex min-h-0 scroll-mt-24 items-start lg:min-h-[70vh] lg:scroll-mt-28">
                <div
                  className={`w-full rounded-2xl border p-5 transition-colors sm:p-6 md:p-8 ${
                    isActive ? "border-slate-900/20 bg-white" : "border-slate-900/15 bg-orange-100"
                  }`}
                >
                  <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-800">{section.title}</p>
                  <h3 className="mt-3 text-2xl font-black leading-tight text-slate-950 sm:text-3xl">{section.description}</h3>

                  <div className="mt-6 space-y-3 sm:mt-7 sm:space-y-4">
                    {section.questions.map((question) => (
                      <div
                        key={question}
                        className="flex items-center justify-between gap-4 rounded-2xl border border-slate-200 bg-orange-50 px-4 py-4 sm:gap-5 sm:px-5 sm:py-5"
                      >
                        <p className="text-base font-bold leading-snug text-slate-900 sm:text-lg">{question}</p>
                        <span className="text-2xl font-light leading-none text-orange-800 sm:text-3xl">+</span>
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
